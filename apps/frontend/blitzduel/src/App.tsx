import { RouterProvider } from "react-router-dom";
import "./App.scss";
import { router } from "./Router";
import { PlayerProvider } from "./contexts/PlayerContext";
import { unlockAudio } from "./utils/playSound";

function App() {
  return (
    <div onClick={unlockAudio} onTouchStart={unlockAudio}>
      <PlayerProvider>
        <RouterProvider router={router} />
      </PlayerProvider>
    </div>
  );
}

export default App;
