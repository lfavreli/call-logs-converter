import "./_header.scss"
import { useState } from "react";
import { Link } from "@tanstack/react-router"
import {
    FlareSharp as FlareSharpIcon,
    Menu as MenuIcon
} from "@mui/icons-material";
import {
    AppBar,
    Box,
    Container,
    IconButton,
    Menu,
    MenuItem,
    Toolbar,
    Typography
} from "@mui/material";

const Header = () => {
    const styleMdBreakpoint = { display: { xs: "none", md: "flex" } };
    const styleXsBreakpoint = { display: { xs: "flex", md: "none" } };

    const pages = [
        { title: "HOME", path: "/" },
        { title: "CONTACT", path: "/contact" }
    ];

    const [anchorElNav, setAnchorElNav] = useState(null);

    const handleOpenNavMenu = (event) => {
        setAnchorElNav(event.currentTarget);
    };

    const handleCloseNavMenu = () => {
        setAnchorElNav(null);
    };

    return (
        <AppBar className="header-appbar" position="static">
            <Container className="header-container" maxWidth="xl">
                <Toolbar className="header-toolbar">
                    {/* Medium (md) breakpoint */}
                    <Link to="/">
                        <FlareSharpIcon className="header-toolbar-md-icon" sx={styleMdBreakpoint} />
                    </Link>
                    <Box className="header-toolbar-md-menu-div" sx={styleMdBreakpoint}>
                        {pages.map(({ title, path }) => (
                            <Link
                                key={title}
                                to={path}
                                className="header-toolbar-md-menu-item"
                                activeProps={{ className: "header-toolbar-md-menu-item-active" }}>
                                {title}
                            </Link>
                        ))}
                    </Box>

                    {/* Extra-small (xs) breakpoint */}
                    <Box className="header-toolbar-xs-menu-div" sx={styleXsBreakpoint}>
                        <IconButton
                            className="header-toolbar-xs-menu-icon-btn"
                            size="large"
                            color="inherit"
                            onClick={handleOpenNavMenu}>
                            <MenuIcon />
                        </IconButton>
                        <Menu
                            keepMounted
                            anchorEl={anchorElNav}
                            anchorOrigin={{ vertical: "bottom", horizontal: "left" }}
                            open={Boolean(anchorElNav)}
                            onClose={handleCloseNavMenu}>
                            {pages.map(({ title, path }) => (
                                <MenuItem className="header-toolbar-xs-menu-item" key={title} onClick={handleCloseNavMenu}>
                                    <Link
                                        className="header-toolbar-xs-menu-item-link"
                                        key={title}
                                        to={path}>
                                        <Typography variant="button" color="grey.800">{title}</Typography>
                                    </Link>
                                </MenuItem>
                            ))}
                        </Menu>
                    </Box>
                    <Link to="/" >
                        <FlareSharpIcon className="header-toolbar-xs-icon" sx={styleXsBreakpoint} />
                    </Link>
                </Toolbar>
            </Container>
        </AppBar>
    );
}

export default Header;
