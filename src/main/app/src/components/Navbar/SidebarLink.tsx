import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import React from 'react';
import { NavLink } from 'react-router-dom';
import { ISidebarProps } from './types';
import './style.scss';

export default function SidebarLink(props: ISidebarProps) {
    const {
        url, label, icon, onClick,
    } = props;

    return (
        <div className="link-wrapper">
            <NavLink
                to={url}
                onClick={onClick}
                className={({ isActive }) => (isActive ? 'active-class' : 'non-active-class')}
            >
                <FontAwesomeIcon icon={icon} className="sidebar-icon" />
                <div className="label">{label}</div>
            </NavLink>
        </div>
    );
}