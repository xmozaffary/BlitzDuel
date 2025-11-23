import { RouterProvider } from "react-router-dom";
import "./App.scss";
import { router } from "./Router";
import { PlayerProvider } from "./contexts/PlayerContext";
// CI/CD test
function App() {
  return (
    <PlayerProvider>
      <RouterProvider router={router} />
    </PlayerProvider>
  );
}

export default App;
