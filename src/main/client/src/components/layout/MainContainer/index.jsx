import "./_mainContainer.scss"
import PropTypes from "prop-types";
import { Container } from "@mui/material";

const MainContainer = ({ children }) => {
  return (
    <Container className="main-container" maxWidth="md">
      <main>
        {children}
      </main>
    </Container>
  );
};

MainContainer.propTypes = {
  children: PropTypes.node.isRequired,
};

export default MainContainer;
