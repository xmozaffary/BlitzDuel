import { createBrowserRouter } from "react-router-dom";
import { Layout } from "./pages/Layout";
import { NotFound } from "./pages/NotFound";
import Quizzes from "./pages/Quizzes";
import QuizDetails from "./pages/QuizDetails";
import CreateLobby from "./components/CreateLobby";
import JoinLobby from "./components/JoinLobby";
import GameScreen from "./pages/GameScreen.tsx";
import AuthCallback from "./components/AuthCallback.tsx";
import { ProtctedRouter } from "./components/ProtectedRouterProps.tsx";
import { LoginPage } from "./pages/LoginPage.tsx";



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
        path: "/login",
        element: <LoginPage />,
      },
      {
        path: "/quiz/:quizId",
        element: <QuizDetails />,
      },
      {
        path: "/lobby/create",
        element: <ProtctedRouter>
          <CreateLobby />
        </ProtctedRouter>,
      },
      {
        path: "/lobby/join",
        element: <ProtctedRouter>
          <JoinLobby />
        </ProtctedRouter>,
      },
        {
          path: "/game/:lobbyCode",
          element: <ProtctedRouter>
          <GameScreen />
        </ProtctedRouter>,
        },
          {
          path: "/auth/callback",
          element: <AuthCallback/>
        },
    ],
    errorElement: <NotFound />,
  },
]);
