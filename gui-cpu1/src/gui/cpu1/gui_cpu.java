package gui.cpu1;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
public class gui_cpu extends javax.swing.JFrame {
private Queue<Process> processes = new LinkedList<>();
    public gui_cpu() {
    initComponents();
    }
//nouran omar 
public static Process[] FCFS(Process... processes) {
    Arrays.sort(processes, (a, b) -> a.getArrivalTime() - b.getArrivalTime());
    Queue<Process> inputQueue = new ArrayDeque<>(Arrays.asList(processes));
    Queue<Process> outputQueue = new ArrayDeque<>();
    int currentTime = 0;
    while (!inputQueue.isEmpty()) {
        Process process = inputQueue.poll();
        process.setState("Running");
        if (process.getArrivalTime() > currentTime) {
            currentTime = process.getArrivalTime();
        }
        currentTime += process.getBurstTime();
        process.setCompletionTime(currentTime);
        process.setTurnaroundTime(currentTime - process.getArrivalTime());
        process.setWaitingTime(process.getTurnaroundTime() - process.getBurstTime());
        process.setResponseTime(process.getWaitingTime());
        process.setState("Terminated"); 
        outputQueue.add(process);
    }
    return outputQueue.toArray(new Process[0]);
}
private void executeFCFS(JTable Table_output) {
    List<Process> processList = new ArrayList<>(processes);
    Process[] processArray = processList.toArray(new Process[0]);
    Process[] processed = FCFS(processArray);
    DefaultTableModel model = (DefaultTableModel) Table_output.getModel();
    model.setRowCount(0); 
    for (Process process : processed) {
        model.addRow(new Object[]{
            process.getProcessID(),
            process.getArrivalTime(),
            process.getBurstTime(),
            process.getPriority(),
            process.getResponseTime(),
            process.getTurnaroundTime(),
            process.getCompletionTime(),
            process.getWaitingTime(),
            process.getState() 
        });
    }
}
private void calculateAverages(JTextField avgTurnaroundField, JTextField avgWaitingField) {
    double totalWaitingTime = 0;
    double totalTurnaroundTime = 0;

for (Process process : processes) {
    totalWaitingTime += process.getWaitingTime();
    totalTurnaroundTime += process.getTurnaroundTime();
}
    int n = processes.size();
    double averageWaitingTime = totalWaitingTime / n;
    double averageTurnaroundTime = totalTurnaroundTime / n;
  
    avgWaitingField.setText(String.format("%.2f", averageWaitingTime));
    avgTurnaroundField.setText(String.format("%.2f", averageTurnaroundTime));
}
// هاجر جمال 
public static Process[] SJF(Process... processes) {
    Arrays.sort(processes, Comparator.comparingInt(Process::getArrivalTime).thenComparingInt(Process::getBurstTime));
    List<Process> readyQueue = new ArrayList<>();
    List<Process> completed = new ArrayList<>();
    int currentTime = 0;
    while (completed.size() < processes.length) {
        for (Process process : processes) {
            if (!completed.contains(process) && process.getArrivalTime() <= currentTime && !readyQueue.contains(process)) {
                process.setState("Ready"); 
                readyQueue.add(process);
            }
        }
       if (!readyQueue.isEmpty()) {
            Process nextProcess = Collections.min(readyQueue, Comparator.comparingInt(Process::getBurstTime));
            readyQueue.remove(nextProcess);
            nextProcess.setState("Running");
            currentTime = Math.max(currentTime, nextProcess.getArrivalTime()) + nextProcess.getBurstTime();
            nextProcess.setCompletionTime(currentTime);
            nextProcess.setTurnaroundTime(nextProcess.getCompletionTime() - nextProcess.getArrivalTime());
            nextProcess.setWaitingTime(nextProcess.getTurnaroundTime() - nextProcess.getBurstTime());
            nextProcess.setResponseTime(nextProcess.getWaitingTime());
            nextProcess.setState("Terminated");
            completed.add(nextProcess);
        } else {
            currentTime++;
        }
    }

    return completed.toArray(new Process[0]);
}
private void executeSJF(JTable Table_output) {
    List<Process> processList = new ArrayList<>(processes);
    Process[] processArray = processList.toArray(new Process[0]);
    Process[] processed = SJF(processArray);
    DefaultTableModel model = (DefaultTableModel) Table_output.getModel();
    model.setRowCount(0); 
    for (Process process : processed) {
        model.addRow(new Object[]{
            process.getProcessID(),
            process.getArrivalTime(),
            process.getBurstTime(),
            process.getPriority(),
            process.getResponseTime(),
            process.getTurnaroundTime(),
            process.getCompletionTime(),
            process.getWaitingTime(),
            process.getState() 
        });
    }
}
//ضي عطيه 
public static Process[] RoundRobin(int timeQuantum, Process... processes) {
    Arrays.sort(processes, Comparator.comparingInt(Process::getArrivalTime)); 
    Queue<Process> readyQueue = new ArrayDeque<>();
    List<Process> completed = new ArrayList<>();
    int currentTime = 0;
    for (Process process : processes) {
        process.setRemainingTime(process.getBurstTime());
        process.setState("Ready"); 
    }
    while (!readyQueue.isEmpty() || Arrays.stream(processes).anyMatch(p -> p.getRemainingTime() > 0)) {
        for (Process process : processes) {
            if (process.getArrivalTime() <= currentTime && process.getRemainingTime() > 0 && !readyQueue.contains(process)) {
                process.setState("Ready"); 
                readyQueue.add(process);
            }
        }
        if (!readyQueue.isEmpty()) {
            Process currentProcess = readyQueue.poll(); 
            currentProcess.setState("Running");
            int executionTime = Math.min(timeQuantum, currentProcess.getRemainingTime());
            currentTime += executionTime;
            currentProcess.setRemainingTime(currentProcess.getRemainingTime() - executionTime);
            if (currentProcess.getRemainingTime() == 0) {
                currentProcess.setCompletionTime(currentTime);
                currentProcess.setTurnaroundTime(currentProcess.getCompletionTime() - currentProcess.getArrivalTime());
                currentProcess.setWaitingTime(currentProcess.getTurnaroundTime() - currentProcess.getBurstTime());
                currentProcess.setResponseTime(currentProcess.getWaitingTime());
                currentProcess.setState("Terminated");
                completed.add(currentProcess);
            } else {
                currentProcess.setState("Terminated");
                readyQueue.add(currentProcess);
            }
        } else {
            currentTime++;
        }
    }
    return completed.toArray(new Process[0]);
}
private void executeRoundRobin(int timeQuantum, JTable Table_output) {
    List<Process> processList = new ArrayList<>(processes);
    Process[] processArray = processList.toArray(new Process[0]);
    Process[] processed = RoundRobin(timeQuantum, processArray);
    DefaultTableModel model = (DefaultTableModel) Table_output.getModel();
    model.setRowCount(0); 
    for (Process process : processed) {
        model.addRow(new Object[]{
            process.getProcessID(),
            process.getArrivalTime(),
            process.getBurstTime(),
            process.getPriority(),
            process.getResponseTime(),
            process.getTurnaroundTime(),
            process.getCompletionTime(),
            process.getWaitingTime(),
            process.getState() 
        });
    }
}
//Mahmoud Khaled Mohamed Attya

public static Process[] PriorityScheduling(Process... processes) {
    // ترتيب العمليات حسب الأولوية (الأولوية الأقل تعني أولوية أعلى)
    Arrays.sort(processes, Comparator.comparingInt(Process::getPriority).thenComparingInt(Process::getArrivalTime));

    List<Process> completed = new ArrayList<>();
    int currentTime = 0;

    for (Process process : processes) {
        process.setRemainingTime(process.getBurstTime());
        process.setState("Ready"); // تعيين الحالة إلى Ready
    }

    for (Process process : processes) {
        if (process.getArrivalTime() > currentTime) {
            currentTime = process.getArrivalTime(); // تحديث الزمن الحالي إذا لم تصل العملية بعد
        }

        process.setState("Running"); // تعيين الحالة إلى Running
        currentTime += process.getBurstTime(); // تنفيذ العملية بالكامل

        process.setCompletionTime(currentTime);
        process.setTurnaroundTime(process.getCompletionTime() - process.getArrivalTime());
        process.setWaitingTime(process.getTurnaroundTime() - process.getBurstTime());
        process.setResponseTime(process.getWaitingTime()); // لأن الاستجابة في الأولوية تعتمد على وقت الانتظار
        process.setState("Terminated"); // تعيين الحالة إلى Terminated

        completed.add(process);
    }

    return completed.toArray(new Process[0]);
}

private void executePriorityScheduling(JTable Table_output) {
    List<Process> processList = new ArrayList<>(processes);
    Process[] processArray = processList.toArray(new Process[0]);
    Process[] processed = PriorityScheduling(processArray);

    DefaultTableModel model = (DefaultTableModel) Table_output.getModel();
    model.setRowCount(0); // مسح الجدول قبل إضافة النتائج الجديدة

    for (Process process : processed) {
        model.addRow(new Object[]{
            process.getProcessID(),
            process.getArrivalTime(),
            process.getBurstTime(),
            process.getPriority(),
            process.getResponseTime(),
            process.getTurnaroundTime(),
            process.getCompletionTime(),
            process.getWaitingTime(),
            process.getState()
        });
    }
}

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        Time_Quantum_for_RR_Algorithm = new javax.swing.JLabel();
        Select_Scheduling_Method = new javax.swing.JLabel();
        Calculate = new javax.swing.JButton();
        Time_Quantum_for_RR_Algorithm_input = new javax.swing.JTextField();
        Select_Scheduling_Method_Input = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_input = new javax.swing.JTable();
        Process_ID = new javax.swing.JLabel();
        Arrival_Time = new javax.swing.JLabel();
        Burst_Time = new javax.swing.JLabel();
        Priority = new javax.swing.JLabel();
        process_id_input = new javax.swing.JTextField();
        Arrival_Time_input = new javax.swing.JTextField();
        Burst_Time_input = new javax.swing.JTextField();
        Priority_input = new javax.swing.JTextField();
        Add = new javax.swing.JButton();
        Clear = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Table_output = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        Average_Watting_Time = new javax.swing.JLabel();
        Avrage_turingtime = new javax.swing.JLabel();
        Avrage_turingtime_input = new javax.swing.JTextField();
        Average_Watting_Time_input = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(204, 204, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Cpu Schdling algorithm", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Handwriting", 1, 12), new java.awt.Color(153, 153, 255))); // NOI18N

        jPanel2.setBackground(new java.awt.Color(204, 153, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "algorithm", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        Time_Quantum_for_RR_Algorithm.setText("Time Quantum for RR Algorithm:");

        Select_Scheduling_Method.setText("Select Scheduling Method:");

        Calculate.setBackground(new java.awt.Color(204, 204, 255));
        Calculate.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        Calculate.setText("Calculate");
        Calculate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CalculateActionPerformed(evt);
            }
        });

        Time_Quantum_for_RR_Algorithm_input.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Time_Quantum_for_RR_Algorithm_inputActionPerformed(evt);
            }
        });

        Select_Scheduling_Method_Input.setBackground(new java.awt.Color(204, 204, 255));
        Select_Scheduling_Method_Input.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        Select_Scheduling_Method_Input.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Scheduling Method", "FCFS (First Come First Serve)", "Priority Scheduling", "RoundRobin (RR) Scheduling", "Shortest-Job-First (SJF) Scheduling" }));
        Select_Scheduling_Method_Input.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Select_Scheduling_Method_InputActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Time_Quantum_for_RR_Algorithm, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Select_Scheduling_Method_Input, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Time_Quantum_for_RR_Algorithm_input, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(Calculate, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(Select_Scheduling_Method, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Select_Scheduling_Method, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(Select_Scheduling_Method_Input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addComponent(Time_Quantum_for_RR_Algorithm)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Time_Quantum_for_RR_Algorithm_input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addComponent(Calculate)
                .addGap(22, 22, 22))
        );

        jPanel3.setBackground(new java.awt.Color(204, 153, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "input section", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        Table_input.setBackground(new java.awt.Color(204, 204, 255));
        Table_input.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Process ID", "Arrival Time", "Burst Time", "Priority", "State"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(Table_input);

        Process_ID.setText("Process ID:");

        Arrival_Time.setText("Arrival Time:");

        Burst_Time.setText("Burst Time:");

        Priority.setText("Priority:");

        Arrival_Time_input.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Arrival_Time_inputActionPerformed(evt);
            }
        });

        Add.setBackground(new java.awt.Color(204, 204, 255));
        Add.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        Add.setText("Add Process");
        Add.setFocusCycleRoot(true);
        Add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddActionPerformed(evt);
            }
        });

        Clear.setBackground(new java.awt.Color(204, 204, 255));
        Clear.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        Clear.setText("Clear Table");
        Clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ClearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Process_ID)
                    .addComponent(Arrival_Time))
                .addGap(32, 32, 32)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(process_id_input, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(50, 50, 50)
                        .addComponent(Burst_Time))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(Arrival_Time_input, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(50, 50, 50)
                        .addComponent(Priority)))
                .addGap(31, 31, 31)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Priority_input, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Burst_Time_input, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(Add, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                    .addComponent(Clear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(135, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Process_ID)
                    .addComponent(process_id_input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Burst_Time)
                    .addComponent(Burst_Time_input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Add))
                .addGap(27, 27, 27)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Arrival_Time)
                    .addComponent(Arrival_Time_input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Priority)
                    .addComponent(Priority_input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Clear))
                .addGap(445, 445, 445))
        );

        jPanel4.setBackground(new java.awt.Color(204, 153, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "output section", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        Table_output.setBackground(new java.awt.Color(204, 204, 255));
        Table_output.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        Table_output.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Process ID", "Arrival Time", "Burst Time", "Priority", "Response Time", "Turnaround Time", "Completion Time", "Waiting Time", "State"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_output.setSelectionBackground(new java.awt.Color(204, 204, 255));
        jScrollPane2.setViewportView(Table_output);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
        );

        jPanel5.setBackground(new java.awt.Color(204, 153, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        Average_Watting_Time.setText("Average Watting Time :");

        Avrage_turingtime.setText("Avrage turingtime :");

        Avrage_turingtime_input.setBackground(new java.awt.Color(102, 102, 102));
        Avrage_turingtime_input.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        Avrage_turingtime_input.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Avrage_turingtime_input.setDisabledTextColor(new java.awt.Color(255, 255, 255));
        Avrage_turingtime_input.setEnabled(false);
        Avrage_turingtime_input.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Avrage_turingtime_inputActionPerformed(evt);
            }
        });

        Average_Watting_Time_input.setBackground(new java.awt.Color(102, 102, 102));
        Average_Watting_Time_input.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        Average_Watting_Time_input.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Average_Watting_Time_input.setDisabledTextColor(new java.awt.Color(255, 255, 255));
        Average_Watting_Time_input.setEnabled(false);
        Average_Watting_Time_input.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Average_Watting_Time_inputActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Avrage_turingtime_input, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Average_Watting_Time_input, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Average_Watting_Time, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Avrage_turingtime, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(Average_Watting_Time)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Avrage_turingtime_input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(Avrage_turingtime, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Average_Watting_Time_input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(53, 53, 53))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void Arrival_Time_inputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Arrival_Time_inputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Arrival_Time_inputActionPerformed

    private void Time_Quantum_for_RR_Algorithm_inputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Time_Quantum_for_RR_Algorithm_inputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Time_Quantum_for_RR_Algorithm_inputActionPerformed

    private void CalculateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CalculateActionPerformed
   String selectedMethod = (String) Select_Scheduling_Method_Input.getSelectedItem();

 if ("FCFS (First Come First Serve)".equals(selectedMethod)) {
      
       executeFCFS(Table_output);
       calculateAverages(Average_Watting_Time_input,Avrage_turingtime_input);

    } else if ("Shortest-Job-First (SJF) Scheduling".equals(selectedMethod)) {
    executeSJF(Table_output);
    calculateAverages(Average_Watting_Time_input, Avrage_turingtime_input);
}
 else if ("RoundRobin (RR) Scheduling".equals(selectedMethod)) {
    try {
        int timeQuantum = Integer.parseInt(Time_Quantum_for_RR_Algorithm_input.getText().trim());
        executeRoundRobin(timeQuantum, Table_output);
        calculateAverages(Average_Watting_Time_input, Avrage_turingtime_input);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Please enter a valid Time Quantum!", "Error", JOptionPane.ERROR_MESSAGE);
    }
}
else if ("Priority Scheduling".equals(selectedMethod)) {
    executePriorityScheduling(Table_output);
    calculateAverages(Average_Watting_Time_input, Avrage_turingtime_input);
    } else {
       
        JOptionPane.showMessageDialog(null, "Please select a valid scheduling method!", "Error", JOptionPane.ERROR_MESSAGE);
    }    
    }//GEN-LAST:event_CalculateActionPerformed

    private void Average_Watting_Time_inputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Average_Watting_Time_inputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Average_Watting_Time_inputActionPerformed

    private void Avrage_turingtime_inputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Avrage_turingtime_inputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Avrage_turingtime_inputActionPerformed
    private void AddActionPerformed(java.awt.event.ActionEvent evt) {
    if (process_id_input.getText().trim().equals("") || 
        Arrival_Time_input.getText().trim().equals("") || 
        Burst_Time_input.getText().trim().equals("") || 
        Priority_input.getText().trim().equals("")) {
        JOptionPane.showMessageDialog(null, "Please enter all input numbers!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    try {
        int processID = Integer.parseInt(process_id_input.getText().trim());
        int arrivalTime = Integer.parseInt(Arrival_Time_input.getText().trim());
        int burstTime = Integer.parseInt(Burst_Time_input.getText().trim());
        int priority = Integer.parseInt(Priority_input.getText().trim());

        Process newProcess = new Process(processID, arrivalTime, burstTime, priority);
        processes.add(newProcess);
//        JOptionPane.showMessageDialog(null, "Process added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);


        DefaultTableModel model = (DefaultTableModel) Table_input.getModel();
        model.addRow(new Object[]{processID, arrivalTime, burstTime, priority,"Ready"});

        process_id_input.setText("");
        Arrival_Time_input.setText("");
        Burst_Time_input.setText("");
        Priority_input.setText("");
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Invalid input Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}
   
    private void ClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClearActionPerformed
    DefaultTableModel model = (DefaultTableModel) Table_input.getModel();
    model.setRowCount(0); 
    DefaultTableModel output = (DefaultTableModel) Table_output.getModel();
    output.setRowCount(0);
    processes.clear(); 
    Average_Watting_Time_input.setText("0.0");
    Time_Quantum_for_RR_Algorithm_input.setText("0.0");
    Avrage_turingtime_input.setText("0.0"); 
    }//GEN-LAST:event_ClearActionPerformed

    private void Select_Scheduling_Method_InputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Select_Scheduling_Method_InputActionPerformed
    
    }//GEN-LAST:event_Select_Scheduling_Method_InputActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new gui_cpu().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Add;
    private javax.swing.JLabel Arrival_Time;
    private javax.swing.JTextField Arrival_Time_input;
    private javax.swing.JLabel Average_Watting_Time;
    private javax.swing.JTextField Average_Watting_Time_input;
    private javax.swing.JLabel Avrage_turingtime;
    private javax.swing.JTextField Avrage_turingtime_input;
    private javax.swing.JLabel Burst_Time;
    private javax.swing.JTextField Burst_Time_input;
    private javax.swing.JButton Calculate;
    private javax.swing.JButton Clear;
    private javax.swing.JLabel Priority;
    private javax.swing.JTextField Priority_input;
    private javax.swing.JLabel Process_ID;
    private javax.swing.JLabel Select_Scheduling_Method;
    private javax.swing.JComboBox<String> Select_Scheduling_Method_Input;
    private javax.swing.JTable Table_input;
    private javax.swing.JTable Table_output;
    private javax.swing.JLabel Time_Quantum_for_RR_Algorithm;
    private javax.swing.JTextField Time_Quantum_for_RR_Algorithm_input;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField process_id_input;
    // End of variables declaration//GEN-END:variables
}