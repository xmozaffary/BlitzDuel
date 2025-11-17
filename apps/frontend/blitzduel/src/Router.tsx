import { createBrowserRouter } from "react-router-dom";
import { Layout } from "./pages/Layout";
import { NotFound } from "./pages/NotFound";
import Quizzes from "./pages/Quizzes";
import QuizDetails from "./pages/QuizDetails";
import CreateLobby from "./components/CreateLobby";
import JoinLobby from "./components/JoinLobby";
import GameScreen from "./pages/GameScreen.tsx";

export const router = createBrowserRouter([
  {
    path: "/",
    element: <Layout />,
    children: [
      {
        path: "/",
        element: <Quizzes />,
      },
      {
        path: "/quiz/:quizId",
        element: <QuizDetails />,
      },
      {
        path: "/lobby/create",
        element: <CreateLobby />,
      },
      {
        path: "/lobby/join",
        element: <JoinLobby />,
      },
        {
          path: "/game/:lobbyCode",
          element: <GameScreen/>
        },
    ],
    errorElement: <NotFound />,
  },
]);
