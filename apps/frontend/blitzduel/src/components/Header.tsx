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
                <NavLink to={"/Web"}>Web</NavLink>
              </li>
              <li>
                <NavLink to={"/om"}>Om</NavLink>
              </li>
            </ul>
          </nav>
        </header>
      </section>
    </>
  );
};
