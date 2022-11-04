import React from 'react';
import {
    BrowserRouter as Router,
    Route,
    Routes,
} from 'react-router-dom';
import { QueryClient, QueryClientProvider } from "react-query";
import Login from "./pages/Login/Login";
import RequireAuth from './components/RequireAuth/RequireAuth';
import Home from './pages/Home/Home';
import {UserProvider} from "./context/UserContext";
import Signup from './pages/Signup/Signup';
import './App.css';
import { PaymentProvider } from './context/PaymentContext';

function App() {
  const queryClient = new QueryClient();

  return (
    <div className="App">
      <QueryClientProvider client={queryClient}>
        <UserProvider>
          <PaymentProvider>
            <Router>
              <Routes>
                <Route path="/login" element={(
                      <Login />
                    )} />
                <Route path="/signup" element={(
                      <Signup />
                    )} />
                <Route
                    path="/*"
                    element={(
                        <RequireAuth>
                          <Home />
                        </RequireAuth>
                    )}
                />
              </Routes>
            </Router>
          </PaymentProvider>
        </UserProvider>
      </QueryClientProvider>
    </div>
  );
}

export default App;
