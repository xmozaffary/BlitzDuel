export const playSound = (soundName: string) => {
  const audio = new Audio(`/sounds/${soundName}`);
  audio.play().catch((error) => {
    console.log("Could not play sound:", error);
  });
};
