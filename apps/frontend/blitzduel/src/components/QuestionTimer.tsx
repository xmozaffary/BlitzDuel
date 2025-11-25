import { useEffect, useState } from "react";

interface QuestionTimerProps {
    remainingTime: number;
    onTimeUp: () => void;
}

export const QuestionTimer = ({remainingTime,  onTimeUp}: QuestionTimerProps) => {
    const [timeLeft, setTimeLeft] = useState(remainingTime)
    
    useEffect(() => {

        setTimeLeft(remainingTime);


        setTimeLeft(remainingTime);

        if(remainingTime <= 0) {
            onTimeUp();
        }
        


    }, [remainingTime, onTimeUp])

    const percentage = (timeLeft / 5) * 100;
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

