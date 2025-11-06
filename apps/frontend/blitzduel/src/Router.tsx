import { createBrowserRouter } from "react-router-dom";
import { Layout } from "./pages/Layout";
import  WebSocket  from "./components/WebSocket";
import { NotFound } from "./pages/NotFound";
import QuizSelection from "./pages/QuizSelection"
import QuizDetails from "./pages/QuizDetails";

export const router = createBrowserRouter([
  {
    path: "/",
    element: <Layout />,
    children: [
      {
        path: "/",
        element: <QuizSelection />,
      },
        {
          path: "/quiz/:quizId",
          element: <QuizDetails />
        },
        {
          path: "/Web",
          element: <WebSocket />,
        }
    ],
    errorElement: <NotFound />,
  },
]);
