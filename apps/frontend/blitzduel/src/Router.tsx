import { createBrowserRouter } from "react-router-dom";
import { Layout } from "./pages/Layout";
import { Home } from "./pages/Home";
// import { Games } from "./pages/Games";
// import { Game } from "./pages/Game";
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
      //   {
      //     path: "/Games",
      //     element: <Games />,
      //   },
      //   {
      //     path: "/game/:id",
      //     element: <Game />,
      //   },
    ],
    errorElement: <NotFound />,
  },
]);
