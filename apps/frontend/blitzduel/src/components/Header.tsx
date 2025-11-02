import { NavLink } from "react-router-dom";

export const Header = () => {
  return (
    <>
      <section className="headerContainer">
        <header className="header">
          <nav className="navigation">
            <ul>
              <li>
                <NavLink to={"/"}>Hem</NavLink>
              </li>
              <li>
                <NavLink to={"/games"}>Games</NavLink>
              </li>
              <li>
                <NavLink to={"/game"}>Om</NavLink>
              </li>
            </ul>
          </nav>
        </header>
      </section>
    </>
  );
};
