interface ButtonProps {
  text: string;
  onClick: () => void;
  variant?: "primary" | "secondary" | "ready";
  disabled?: boolean;
  size?: "small" | "medium" | "large";
}

export const Button = ({
  text,
  onClick,
  variant = "primary",
  disabled = false,
}: ButtonProps) => {
  return (
    <button
      className={`button button-${variant}`}
      onClick={onClick}
      disabled={disabled}
    >
      {text}
    </button>
  );
};
