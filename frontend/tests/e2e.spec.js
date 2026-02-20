// tests/e2e.spec.js
import { test, expect } from "@playwright/test";

test.use({
  headless: false,
  viewport: { width: 1280, height: 800 },
});

test.describe("Email Notification System - Full Flow", () => {
  const baseURL = "http://localhost:5173";

  // -----------------------
  // Dashboard Page
  // -----------------------
  test("Dashboard charts render correctly", async ({ page }) => {
    await page.goto(`${baseURL}/dashboard`);

    // Wait for dashboard API
    await page.waitForResponse(
      (resp) => resp.url().includes("/reports/dashboard") && resp.status() === 200
    );

    // Wait for Daily Chart
    const dailyChart = page.locator('text=Daily Email Counts (Last 7 Days)');
    await expect(dailyChart).toBeVisible();

    // Wait for Status Chart
    const statusChart = page.locator('text=Email Status Counts');
    await expect(statusChart).toBeVisible();
  });

  // -----------------------
  // Emails Page
  // -----------------------
  test("Compose and view email", async ({ page }) => {
    await page.goto(`${baseURL}/emails`);

    // Wait for emails API
    await page.waitForResponse(
      (resp) => resp.url().includes("/emails") && resp.status() === 200
    );

    // Open Compose Email Modal
    // Open Compose Email Modal
await page.click('text=Compose Email');

// Wait for modal container to appear
const modal = page.locator('div[role="dialog"]'); // modal container
await expect(modal).toBeVisible();

// Fill form inside the modal
await modal.locator('input[placeholder="noreply@yourapp.com"]').fill('thrishatha.arvin@growfin.ai');
await modal.locator('input[placeholder="recipient@example.com"]').fill('savinsha@gmail.com');
await modal.locator('input[id="sub"]').fill('Playwright Test Email');
await modal.locator('textarea[id="body"]').fill('Hello! This is a test email from Playwright.');

// Click Send inside modal
await modal.locator('text=Send').click();

// Wait for success notification
await page.waitForSelector(
  'div.ant-message-success:has-text("Email queued successfully!")',
  { timeout: 10000 }
);


    // Open first email in table
    const firstEmailRow = page.locator('table tbody tr').first();
    await firstEmailRow.locator('text=View').click();

    // Wait for email details page
    await page.waitForSelector('text=Email Details', { timeout: 5000 });
    const subjectText = await page.locator('p:has-text("Subject:")').textContent();
    expect(subjectText).toContain('Playwright Test Email');

    // Back to Emails
    await page.click('text=Back');
  });

  // -----------------------
  // Templates Page
  // -----------------------
  test("Create template and send email from template", async ({ page }) => {
    await page.goto(`${baseURL}/templates`);

    // Wait for templates API
    await page.waitForResponse(
      (resp) => resp.url().includes("/templates") && resp.status() === 200
    );

    // Click New Template
    await page.click('text=New Template');

    // Fill template form using placeholders
    await page.fill('input[id="name"]', 'Playwright Template');
    await page.fill('input[id="sub"]', 'Template Subject');
    await page.fill('textarea[id="body"]', 'Template Body');

    await page.click('text=Create Template');

    // Wait for success notification
    await page.waitForSelector(
      'div.ant-message-success:has-text("Template created successfully")',
      { timeout: 10000 }
    );

    // Use the first template in the table
    const firstTemplateRow = page.locator('table tbody tr').first();
    await firstTemplateRow.locator('text=Use').click();

    // Fill email modal using placeholders
    await page.fill('input[placeholder="Your email"]', 'thrishatha.arvin@growfin.ai');
    await page.fill('input[placeholder="Recipient email"]', 'savinsha@gmail.com');

    // Click Send
    await page.click('text=Send');

    // Wait for success notification
    await page.waitForSelector(
      'div.ant-message-success:has-text("Email sent successfully!")',
      { timeout: 10000 }
    );
  });
});
