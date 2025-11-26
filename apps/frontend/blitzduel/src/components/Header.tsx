import { NavLink, Link } from "react-router-dom";

export const Header = () => {
  return (
    <section className="headerContainer">
      <header className="header">
        <nav className="navigation">
          <ul>
            <li className="logo-item">
              <Link to="/" className="logo-link">
                <img 
                  src="/assets/blitzduel-logo.jpg" 
                  alt="BlitzDuel Logo" 
                  className="blitzduel-logo"
                />
              </Link>
            </li>

            <li>
              <NavLink to={"/lobby/join"}>Join lobby</NavLink>
            </li>
          </ul>
        </nav>
      </header>
    </section>
  );
};





// import { NavLink } from "react-router-dom";

// export const Header = () => {
//   return (
//     <>
//       <section className="headerContainer">
//         <header className="header">
//           <nav className="navigation">
//             <ul>
//               <li>
//                 <NavLink to={"/"}>Hem</NavLink>
//               </li>
//               <li>
//                 <NavLink to={"/lobby/join"}>Join lobby</NavLink>
//               </li>
//             </ul>
//           </nav>
//         </header>
//       </section>
//     </>
//   );
// };
