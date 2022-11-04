import React from "react";
import {
    faArrowRightFromBracket,
    faCreditCard,
    faHouse,
    faUser,
} from "@fortawesome/free-solid-svg-icons";
import SidebarLink from "./SidebarLink";
import { useUserContext } from "../../context/UserContext";
import "./style.scss";

export default function Navbar() {
    const { logout } = useUserContext();

    return (
        <div className="navbar-container">
            <div className="navbar-list">
                <SidebarLink url="/" label="Home" icon={faHouse} />
                <SidebarLink url="/profile" label="Profile" icon={faUser} />
                <SidebarLink url="/payment" label="Payment" icon={faCreditCard} />
                <SidebarLink url="/login" label="Logout" onClick={() => logout()} icon={faArrowRightFromBracket} />
            </div>
        </div>
    );
}