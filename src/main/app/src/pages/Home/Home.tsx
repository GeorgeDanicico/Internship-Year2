import Navbar from '../../components/Navbar/Navbar';
import RequireAuth from '../../components/RequireAuth/RequireAuth';
import { Route, Routes } from "react-router-dom";
import Dashboard from "../Dashboard/Dashboard";
import './style.scss';
import AddAccount from '../AddAccount/AddAccount';
import RedirectBT from '../Redirect/RedirectBT';
import RedirectBRD from '../Redirect/RedirectBRD';
import Profile from '../Profile/Profile';
import Payment from '../Payment/Payment';
import SuccessfulBTPayment from '../SuccessfulPayment/SuccessfulBTPayment';
import AccountPayments from '../AccountPayments/AccountPayments';
import RedirectCec from '../Redirect/RedirectCec';
import SuccessfulCecPayment from '../SuccessfulPayment/SuccessfulCecPayment';

export default function Home() {
    return (
        <div className="home-container">
            <Navbar />

            <div className="content">
                <Routes>
                    <Route
                        path="/"
                        element={(
                            <RequireAuth>
                                <Dashboard />
                            </RequireAuth>
                        )}
                    />

                    <Route
                        path="/profile"
                        element={(
                            <RequireAuth>
                                <Profile />
                            </RequireAuth>
                        )}
                    />

                    <Route
                        path="/add_account"
                        element={(
                            <RequireAuth>
                                <AddAccount />
                            </RequireAuth>
                        )}
                    />

                    <Route
                        path="/payment"
                        element={(
                            <RequireAuth>
                                <Payment />
                            </RequireAuth>
                        )}
                    />

                    <Route
                        path="/payments/:accountId"
                        element={(
                            <RequireAuth>
                                <AccountPayments />
                            </RequireAuth>
                        )}
                    />

                    <Route
                        path="/success-payment"
                        element={(
                            <RequireAuth>
                                <SuccessfulBTPayment />
                            </RequireAuth>
                        )}
                    />

                    <Route
                        path="/success-payment-cec"
                        element={(
                            <RequireAuth>
                                <SuccessfulCecPayment />
                            </RequireAuth>
                        )}
                    />

                    <Route
                        path="/redirect-bt"
                        element={(
                            <RequireAuth>
                                <RedirectBT />
                            </RequireAuth>
                        )}
                    />

                    <Route
                        path="/redirect-brd"
                        element={(
                            <RequireAuth>
                                <RedirectBRD />
                            </RequireAuth>
                        )}
                    />

                    <Route
                        path="/redirect-cec"
                        element={(
                            <RequireAuth>
                                <RedirectCec />
                            </RequireAuth>
                        )}
                    />
                </Routes>
            </div>
        </div>
    );
}