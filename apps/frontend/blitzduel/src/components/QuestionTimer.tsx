import { useEffect, useState } from "react";

interface QuestionTimerProps {
    timeLimit: number;
    startTime: number;
    onTimeUp: () => void;
}

export const QuestionTimer = ({timeLimit, startTime, onTimeUp}: QuestionTimerProps) => {
    const [timeLeft, setTimeLeft] = useState(timeLimit)
    
    useEffect(() => {
        const now = Date.now();
        const elapsed = (now -startTime) / 1000;
        const remaining = Math.max(0, timeLimit - elapsed);

        setTimeLeft(remaining);
        
        const interval = setInterval(() => {
            const currentTime = Date.now();
            const elapsedTime = (currentTime - startTime) / 1000;
            const remainingTime = Math.max(0, timeLimit - elapsedTime);

            setTimeLeft(remainingTime);

            if(remainingTime <= 0) {
                clearInterval(interval);
                onTimeUp();
            }
        }, 100);

        return () => clearInterval(interval);


    }, [startTime, timeLimit, onTimeUp])

    const percentage = (timeLeft / timeLimit) * 100;
    const isLowTime = timeLeft <= 2;





    return (
        <div className="question-timer">
            <div className="timer-display">
                <span className={isLowTime ? 'time-warning' : ''}>{Math.ceil(timeLeft)}s</span>
            </div>
            
            <div className="timer-bar">
                <div
                    className={`timer-progress ${isLowTime ? 'low-time' : ''}`}
                    style={{ width: `${percentage}%` }}
                />
            </div >
        </div>
    )
    
}

