import { createBrowserRouter } from "react-router-dom";
import { Layout } from "./pages/Layout";
import { NotFound } from "./pages/NotFound";
import Quizzes from "./pages/Quizzes";
import QuizDetails from "./pages/QuizDetails";
import CreateLobby from "./components/CreateLobby";
import JoinLobby from "./components/JoinLobby";
import GameScreen from "./pages/GameScreen.tsx";
import AuthCallback from "./components/AuthCallback.tsx";
import { LoginPage } from "./pages/LoginPage.tsx";
import { RequiredAuth } from "./components/RequireAuth.tsx";
import { ProfilePage } from "./pages/ProfilePage.tsx";

export const router = createBrowserRouter([
  {
    path: "/login",
    element: <LoginPage />,
  },
  {
    path: "/auth/callback",
    element: <AuthCallback />,
  },
  {
    path: "/",
    element: <Layout />,
    children: [
      {
        path: "/",
        element: (
          <RequiredAuth>
            <Quizzes />
          </RequiredAuth>
        ),
      },
      {
        path: "/quiz/:quizId",
        element: (
          <RequiredAuth>
            <QuizDetails />
          </RequiredAuth>
        ),
      },
      {
        path: "/lobby/create",
        element: (
          <RequiredAuth>
            <CreateLobby />
          </RequiredAuth>
        ),
      },
      {
        path: "/lobby/join",
        element: (
          <RequiredAuth>
            <JoinLobby />
          </RequiredAuth>
        ),
      },
      {
        path: "/game/:lobbyCode",
        element: (
          <RequiredAuth>
            <GameScreen />
          </RequiredAuth>
        ),
      },
      {
        path: "/profile",
        element: (
          <RequiredAuth>
            <ProfilePage />
          </RequiredAuth>
        ),
      },
    ],
    errorElement: <NotFound />,
  },
]);
