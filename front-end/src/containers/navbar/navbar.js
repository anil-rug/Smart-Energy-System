import React, { Component } from "react";
import { Link } from "react-router-dom";
import { Navbar, Nav, Button } from 'react-bootstrap';
// import { LinkContainer } from 'react-router-bootstrap'

import {
    ImageLogo,
    ImageLogoText
}
    from './navbarStyles';
import logo from '../../assets/images/logo-main.png';

class NavBar extends Component {
    render() {
        return (
            <React.Fragment>
                <Navbar bg="dark" variant="dark">
                    <Navbar.Brand href="/">
                        <ImageLogo>
                            <img src={logo} alt="" style={{ height: "5rem", borderRight: "solid white" }} />
                            <ImageLogoText>
                                Smart Energy System
                        </ImageLogoText>
                        </ImageLogo>
                    </Navbar.Brand>
                    <Nav className="mr-auto">
                        <Nav.Link ><Link to="/dashboard">Dashboard</Link></Nav.Link>
                        {/* <Link to="/">Home</Link> */}
                        {/* <Link to="/dashboard">Dashboard</Link> */}
                        {/* <Nav.Link href="#admin">Admin</Nav.Link> */}
                    </Nav>
                </Navbar>
            </React.Fragment>
        );
    }
}

export default NavBar;