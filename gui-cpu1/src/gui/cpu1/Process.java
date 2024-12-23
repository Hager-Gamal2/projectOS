package gui.cpu1;
public  class Process {
    private int processID;
    private int arrivalTime;
    private int burstTime;
    private int priority;
    private int completionTime;
    private int turnaroundTime;
    private int waitingTime;
    private int responseTime;
    private int remainingTime; // وقت التنفيذ المتبقي للعملية
    private String state; // الحالة الجديدة للعملية



 public Process(int processID, int arrivalTime, int burstTime, int priority) {
        this.processID = processID;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.state = "New"; // الحالة الافتراضية

    } // Getters and Setters
    public int getProcessID() { return processID; }
    public int getArrivalTime() { return arrivalTime; }
    public int getBurstTime() { return burstTime; }
    public int getPriority() { return priority; }
    public String getState() {
    return state;
}

public void setState(String state) {
    this.state = state;
}
    public int getCompletionTime() { return completionTime; }
    public int getTurnaroundTime() { return turnaroundTime; }
    public int getWaitingTime() { return waitingTime; }
    public int getResponseTime() { return responseTime; }
    public void setCompletionTime(int completionTime) { this.completionTime = completionTime; }
    public void setTurnaroundTime(int turnaroundTime) { this.turnaroundTime = turnaroundTime; }
    public void setWaitingTime(int waitingTime) { this.waitingTime = waitingTime; }
public void setResponseTime(int responseTime) { this.responseTime = responseTime; }
public int getRemainingTime() {
    return remainingTime;
}

public void setRemainingTime(int remainingTime) {
    this.remainingTime = remainingTime;
}}

