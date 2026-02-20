import EmailCard from "./EmailCard";

export default function EmailList({ emails }) {
  return (
    <div>
      {emails.map((email) => (
        <EmailCard key={email.id} email={email} />
      ))}
    </div>
  );
}
