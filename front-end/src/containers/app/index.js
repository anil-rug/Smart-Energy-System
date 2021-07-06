import React from 'react';
import { Route } from 'react-router-dom';
import Home from '../home/home';
import Dashboard from '../dashboard/dashboard';
import NavBar from '../navbar/navbar';

const App = () => (
  <React.Fragment>
    <NavBar></NavBar>
    <div>
      <main>
        <Route exact path="/" component={Home} />
        <Route exact path="/dashboard" component={Dashboard} />
      </main>
    </div>
  </React.Fragment>
)

export default App
