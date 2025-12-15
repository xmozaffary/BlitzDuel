let audioUnlocked = false;

export const unlockAudio = () => {
  if (audioUnlocked) return;

  const audio = new Audio();
  audio
    .play()
    .then(() => {
      audio.pause();
      audioUnlocked = true;
    })
    .catch(() => {
      // Ignore error
    });
};

export const playSound = (soundName: string) => {
  const audio = new Audio(`/sounds/${soundName}`);
  audio.play().catch((error) => {
    console.log("Could not play sound:", error);
  });
};
