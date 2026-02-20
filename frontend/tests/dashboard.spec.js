import { test, expect } from "@playwright/test";

test.describe("Dashboard Page", () => {
  
  test.beforeEach(async ({ page }) => {
    // Navigate to Dashboard
    await page.goto("/dashboard");
  });


  test("charts render correctly", async ({ page }) => {
    // Daily chart
    const dailyChart = page.locator('text=Daily Email Counts (Last 7 Days)');
    await expect(dailyChart).toBeVisible();

    // Status chart
    const statusChart = page.locator('text=Email Status Counts');
    await expect(statusChart).toBeVisible();
  });
});
