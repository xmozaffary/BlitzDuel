import { Link } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";

export const Header = () => {
  const { user, isAuthenticated } = useAuth();

  return (
    <section className="headerContainer">
      <header className="header">
        <nav className="navigation">
          <ul>
            <li className="logo-item">
              <Link to="/" className="logo-link">
                <span className="logo-text">BlitzDuel</span>
              </Link>
            </li>

            {isAuthenticated && user && (
              <li className="profile-item">
                <Link to="/profile" className="profile-link">
                  <img
                    src={user.profilePictureUrl}
                    alt={user.name}
                    className="profile-avatar"
                  />
                  <span className="profile-name">{user.name}</span>
                </Link>
              </li>
            )}
          </ul>
        </nav>
      </header>
    </section>
  );
};
