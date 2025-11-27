import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { fetchUserInfo } from "../services/fetchUserInfo";

const AuthCallback = () => {
    const navigate = useNavigate();

    useEffect(() => {
        const params = new URLSearchParams(window.location.search);
        const token = params.get('token');

        if (token){

            localStorage.setItem('jwt', token);

            fetchUserInfo().then(user => {
                console.log("fetchar...")
                 navigate('/')
            }).catch(err =>{
                console.log(err)
                navigate("/login")
            });
        } else {
            navigate('/login');
        }

    }, [navigate])
    return <div>Loggar in...</div>
}

export default AuthCallback;

