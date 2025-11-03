import { createBrowserRouter } from "react-router-dom";
import { Layout } from "./pages/Layout";
import { Home } from "./pages/Home";
import  WebSocket  from "./components/WebSocket";
import { NotFound } from "./pages/NotFound";

export const router = createBrowserRouter([
  {
    path: "/",
    element: <Layout />,
    children: [
      {
        path: "/",
        element: <Home />,
      },
        {
          path: "/Web",
          element: <WebSocket />,
        }
    ],
    errorElement: <NotFound />,
  },
]);
