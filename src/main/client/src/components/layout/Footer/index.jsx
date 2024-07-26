import "./_footer.scss"
import { Typography } from "@mui/material";
import { Link } from "@tanstack/react-router"

const Footer = () => {
    return (
        <footer>
            <Typography variant="body2">
                <Link className="footer-link" to="/"  >
                    Home
                </Link>
                <Link className="footer-link" to="/contact">
                    Contact
                </Link>
            </Typography>
        </footer>
    );
}

export default Footer;
