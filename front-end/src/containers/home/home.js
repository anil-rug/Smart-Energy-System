import React, { Component } from 'react';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import axios from 'axios';

import logo from '../../assets/images/logo-main.png';

import {
  MainBody, LoginContainer, LoginInnerContainer,
  LoginHeading, LoginTabContainer, LoginOptions, ImageLogo,
  ImageLogoText, LoginCredentials, LoginButton, LoginButtonContainer
}
  from './homeStyles';

class Home extends Component {

  constructor(props) {
    super(props);
    this.state = {
      username: '',
      password: ''
    }
  }

  renderMainLogo = () => {
    return (
      <ImageLogo>
        <img src={logo} alt="" style={{ height: "8.5rem", borderRight: "solid white" }} />
        <ImageLogoText>
          Smart Energy System
          </ImageLogoText>
      </ImageLogo>
    )
  }

  renderLoginButtons = () => {
    return (
      <LoginTabContainer>
        <LoginOptions>
          Sign In
              </LoginOptions>
        <LoginOptions>
          Sign Up
              </LoginOptions>
      </LoginTabContainer>
    )
  }

  renderLoginText = () => {
    return (
      <LoginHeading>
        Hello there, <br /> welcome back
            </LoginHeading>
    )
  }

  handleUserName = (e) => {
    this.setState({ username: e.target.value });
  }

  handlePassword = (e) => {
    this.setState({password: e.target.value});
  }

  renderInputFields = () => {
    return (
      <LoginCredentials>
        <input type="text" placeholder="User Name" onChange={(e) => this.handleUserName(e)} />
        <input type="password" placeholder="Password"  onChange={(e) => this.handlePassword(e)}/>
      </LoginCredentials>
    )
  }

  renderActionButton = () => {
    return (
      <LoginButtonContainer>
        <LoginButton onClick={e => this.loginController(e)}>
          Sign In
      </LoginButton>
      </LoginButtonContainer>
    )
  }

  loginController = (e) => {
    e.preventDefault();
    console.log("I AM CLICKED");
    this.makeLoginApiCall();
    // this.props.history.push('/dashboard');
  }

  handleNavigation = () => {
    this.props.history.push('/dashboard');
  }

  makeLoginApiCall = () => {
    const {username,password}=this.state;
    const url = `/userAuth/${username}/${password}`
    axios.post(url)
      .then((response) => {
        this.handleNavigation();
        console.log(response);
      })
      .catch((error) => {
        console.log(error);
      });
  }

  render() {
    return (
      <MainBody>
        {/* {this.renderMainLogo()} */}
        <LoginContainer>
          <LoginInnerContainer>
            {this.renderLoginButtons()}
            {this.renderLoginText()}
            {this.renderInputFields()}
            {this.renderActionButton()}
          </LoginInnerContainer>
        </LoginContainer>
      </MainBody>
    )
  }
}

const mapStateToProps = () => ({
})

const mapDispatchToProps = dispatch =>
  bindActionCreators(dispatch)

// export default connect(
//   mapStateToProps,
//   mapDispatchToProps
// )(Home)

export default Home;