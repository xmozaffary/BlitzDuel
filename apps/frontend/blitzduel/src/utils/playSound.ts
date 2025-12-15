export const playSound = (soundName: string) => {
  const audio = new Audio(`/sounds/${soundName}.mp3`);
  audio.play().catch((error) => {
    console.log("Could not play sound:", error);
  });
};
