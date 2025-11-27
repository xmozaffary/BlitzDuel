export const fetchUserInfo = async () => {
    const token = localStorage.getItem('jwt');

    const response = await fetch('http://localhost:8080/api/auth/me' , {
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    });

    if (response.ok) {
        const user = await response.json();
        return user;
    }

    throw new Error("Faild to fetch user")
}

