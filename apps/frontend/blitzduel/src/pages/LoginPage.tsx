export const LoginPage = () =>{
    const handleGoogleLogin =() =>{
        window.location.href = "http://localhost:8080/oauth2/authorization/google"
    };

    return (
        <div>
            <h1>Vällkomen till blitzduel</h1>
            <button onClick={handleGoogleLogin}>log in</button>
        </div>
    
    )
}