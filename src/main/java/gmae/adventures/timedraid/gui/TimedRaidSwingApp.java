package gmae.adventures.timedraid.gui;

import gmae.adventures.timedraid.TimedRaidAdventure;
import gmae.adventures.timedraid.TimedRaidState;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class TimedRaidSwingApp {

    private final TimedRaidAdventure adventure = new TimedRaidAdventure();
    private TimedRaidState state;

    private JFrame frame;

    private final JComboBox<TimeDisplayStrategy> strategyBox = new JComboBox<>();

    private final JLabel helpLabel = new JLabel();
    private final JLabel playerLabel = new JLabel();
    private final JLabel posLabel = new JLabel();
    private final JLabel tileLabel = new JLabel();
    private final JLabel lastActionLabel = new JLabel();
    private final JLabel timeLabel = new JLabel();
    private final JLabel roundsLabel = new JLabel();
    private final JLabel progressLabel = new JLabel();
    private final JLabel statusLabel = new JLabel();

    private JButton work, pass, reset, up, down, left, right;
    private JCheckBox showDebug;
    private JButton obj1, obj2, obj3;

    private int prevRoundsRemaining = -1;
    private TimedRaidState.Result prevResult = null;

    private static final int W = 5;
    private static final int H = 5;
    private final JLabel[][] cells = new JLabel[H][W];

    private static final Color OBJ_COLOR = new Color(255, 249, 219);
    private static final Color ACTIVE_COLOR = new Color(230, 245, 255);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TimedRaidSwingApp().start());
    }

    private void start() {
        adventure.reset();
        state = adventure.getState();

        frame = new JFrame("Timed Raid");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setMinimumSize(new Dimension(980, 520));

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(12, 12, 12, 12));
        frame.setContentPane(root);

        root.add(buildTopPanel(), BorderLayout.NORTH);
        root.add(buildCenterPanel(), BorderLayout.CENTER);
        root.add(buildBottomPanel(), BorderLayout.SOUTH);

        refresh("Ready");

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel buildTopPanel() {
        strategyBox.addItem(new WorldTimeDisplay());
        strategyBox.addItem(new ElapsedTimeDisplay());
        strategyBox.addItem(new BothTimeDisplay());

        strategyBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof TimeDisplayStrategy s) setText(s.name());
                return this;
            }
        });
        strategyBox.addActionListener(e -> refresh("Changed time display"));

        helpLabel.setText("<html><b>Goal:</b> finish all objectives before rounds end. "
                + "<b>Round:</b> P1 acts then P2 acts. "
                + "<b>Play:</b> Move to O1/O2/O3 (yellow tiles), then press <b>Work</b>.</html>");

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        JPanel row1 = new JPanel(new BorderLayout(8, 8));
        row1.add(new JLabel("Time display:"), BorderLayout.WEST);
        row1.add(strategyBox, BorderLayout.CENTER);

        JPanel row2 = new JPanel(new BorderLayout());
        row2.add(helpLabel, BorderLayout.CENTER);

        top.add(row1);
        top.add(Box.createVerticalStrut(8));
        top.add(row2);

        return top;
    }

    private JPanel buildCenterPanel() {
        JPanel center = new JPanel(new GridLayout(1, 2, 12, 0));
        center.add(buildInfoPanel());
        center.add(buildRightPanel());
        return center;
    }

    private JPanel buildInfoPanel() {
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBorder(new TitledBorder("Status"));

        info.add(playerLabel);
        info.add(Box.createVerticalStrut(8));
        info.add(posLabel);
        info.add(Box.createVerticalStrut(8));
        info.add(tileLabel);
        info.add(Box.createVerticalStrut(12));
        info.add(timeLabel);
        info.add(Box.createVerticalStrut(6));
        info.add(roundsLabel);
        info.add(Box.createVerticalStrut(6));
        info.add(progressLabel);
        info.add(Box.createVerticalStrut(6));
        info.add(statusLabel);
        info.add(Box.createVerticalStrut(12));
        info.add(lastActionLabel);

        return info;
    }

    private JPanel buildRightPanel() {
        JPanel right = new JPanel(new BorderLayout(10, 10));
        right.add(buildControlsPanel(), BorderLayout.CENTER);
        right.add(buildMapPanel(), BorderLayout.EAST);
        return right;
    }

    private JPanel buildControlsPanel() {
        JPanel controls = new JPanel(new BorderLayout(10, 10));
        controls.setBorder(new TitledBorder("Controls"));

        JPanel dpad = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.BOTH;

        up = new JButton("↑");
        down = new JButton("↓");
        left = new JButton("←");
        right = new JButton("→");

        Dimension btnSize = new Dimension(64, 48);
        up.setPreferredSize(btnSize);
        down.setPreferredSize(btnSize);
        left.setPreferredSize(btnSize);
        right.setPreferredSize(btnSize);

        c.gridx = 1; c.gridy = 0; dpad.add(up, c);
        c.gridx = 0; c.gridy = 1; dpad.add(left, c);
        c.gridx = 1; c.gridy = 1; dpad.add(new JLabel("Move", SwingConstants.CENTER), c);
        c.gridx = 2; c.gridy = 1; dpad.add(right, c);
        c.gridx = 1; c.gridy = 2; dpad.add(down, c);

        work = new JButton("Work");
        pass = new JButton("Pass");
        reset = new JButton("Reset");

        JPanel actionCol = new JPanel(new GridLayout(3, 1, 8, 8));
        actionCol.add(work);
        actionCol.add(pass);
        actionCol.add(reset);

        controls.add(dpad, BorderLayout.CENTER);
        controls.add(actionCol, BorderLayout.EAST);

        up.addActionListener(e -> { state.applyAction(state.getCurrentPlayer(), TimedRaidState.Action.MOVE_UP); refresh("Moved up"); });
        down.addActionListener(e -> { state.applyAction(state.getCurrentPlayer(), TimedRaidState.Action.MOVE_DOWN); refresh("Moved down"); });
        left.addActionListener(e -> { state.applyAction(state.getCurrentPlayer(), TimedRaidState.Action.MOVE_LEFT); refresh("Moved left"); });
        right.addActionListener(e -> { state.applyAction(state.getCurrentPlayer(), TimedRaidState.Action.MOVE_RIGHT); refresh("Moved right"); });

        work.addActionListener(e -> { state.applyAction(state.getCurrentPlayer(), TimedRaidState.Action.WORK); refresh("Work"); });
        pass.addActionListener(e -> { state.applyAction(state.getCurrentPlayer(), TimedRaidState.Action.PASS); refresh("Pass"); });
        reset.addActionListener(e -> { adventure.reset(); state = adventure.getState(); prevRoundsRemaining = -1; prevResult = null; refresh("Reset"); });

        return controls;
    }

    private JPanel buildMapPanel() {
        JPanel mapWrap = new JPanel(new BorderLayout());
        mapWrap.setBorder(new TitledBorder("Map (5x5)"));

        JPanel map = new JPanel(new GridLayout(H, W, 2, 2));
        map.setBorder(new EmptyBorder(8, 8, 8, 8));

        Font f = new Font("SansSerif", Font.BOLD, 14);

        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                JLabel cell = new JLabel("", SwingConstants.CENTER);
                cell.setOpaque(true);
                cell.setBackground(Color.WHITE);
                cell.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));
                cell.setPreferredSize(new Dimension(44, 44));
                cell.setFont(f);
                cells[y][x] = cell;
                map.add(cell);
            }
        }

        JLabel legend = new JLabel("Legend: O1/O2/O3 = objectives (yellow), P1/P2 = players (blue = current turn)", SwingConstants.CENTER);

        mapWrap.add(map, BorderLayout.CENTER);
        mapWrap.add(legend, BorderLayout.SOUTH);

        return mapWrap;
    }

    private JPanel buildBottomPanel() {
        JPanel bottom = new JPanel(new BorderLayout(10, 10));

        showDebug = new JCheckBox("Show debug buttons (direct objectives)");
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.add(showDebug);

        JPanel debug = new JPanel(new GridLayout(1, 3, 8, 0));
        obj1 = new JButton("Obj1");
        obj2 = new JButton("Obj2");
        obj3 = new JButton("Obj3");
        debug.add(obj1);
        debug.add(obj2);
        debug.add(obj3);
        debug.setVisible(false);

        showDebug.addActionListener(e -> {
            debug.setVisible(showDebug.isSelected());
            frame.pack();
        });

        obj1.addActionListener(e -> { state.applyAction(state.getCurrentPlayer(), TimedRaidState.Action.WORK_ON_OBJECTIVE_1); refresh("Obj1 (debug)"); });
        obj2.addActionListener(e -> { state.applyAction(state.getCurrentPlayer(), TimedRaidState.Action.WORK_ON_OBJECTIVE_2); refresh("Obj2 (debug)"); });
        obj3.addActionListener(e -> { state.applyAction(state.getCurrentPlayer(), TimedRaidState.Action.WORK_ON_OBJECTIVE_3); refresh("Obj3 (debug)"); });

        bottom.add(left, BorderLayout.WEST);
        bottom.add(debug, BorderLayout.CENTER);

        return bottom;
    }

    private void refresh(String lastAction) {
        TimeDisplayStrategy strat = (TimeDisplayStrategy) strategyBox.getSelectedItem();
        if (strat == null) strat = new WorldTimeDisplay();

        playerLabel.setText("Current Player: " + state.getCurrentPlayer());

        int p1x = state.getPlayerX(TimedRaidState.PlayerId.P1);
        int p1y = state.getPlayerY(TimedRaidState.PlayerId.P1);
        int p2x = state.getPlayerX(TimedRaidState.PlayerId.P2);
        int p2y = state.getPlayerY(TimedRaidState.PlayerId.P2);
        posLabel.setText("Positions: P1 (" + p1x + "," + p1y + ")   P2 (" + p2x + "," + p2y + ")");

        String tileText = tileAt(state.getCurrentPlayer());
        boolean onObjective = state.isPlayerOnObjective(state.getCurrentPlayer());
        tileLabel.setText("Standing on: " + tileText + (onObjective ? " (Work enabled)" : " (Move to O1/O2/O3 to enable Work)"));

        timeLabel.setText("Time: " + strat.format(state));
        roundsLabel.setText("Rounds Remaining: " + state.getRoundsRemaining());
        progressLabel.setText("Progress: " + state.getObjectivesCompleted() + " / " + state.getObjectivesRequired());
        statusLabel.setText("State: " + state.getResult());

        String roundMsg = "";
        if (prevRoundsRemaining != -1 && state.getRoundsRemaining() < prevRoundsRemaining) {
            roundMsg = " (round ended, time advanced)";
        }
        lastActionLabel.setText("Last action: " + lastAction + roundMsg);

        boolean done = state.isOver();

        up.setEnabled(!done);
        down.setEnabled(!done);
        left.setEnabled(!done);
        right.setEnabled(!done);
        pass.setEnabled(!done);

        work.setEnabled(!done && onObjective);
        work.setToolTipText(onObjective ? "Work on this objective" : "Move onto O1/O2/O3 first");

        if (prevResult != null && prevResult == TimedRaidState.Result.IN_PROGRESS && state.getResult() != TimedRaidState.Result.IN_PROGRESS) {
            JOptionPane.showMessageDialog(frame, "Game Over: " + state.getResult(), "Timed Raid", JOptionPane.INFORMATION_MESSAGE);
        }

        prevRoundsRemaining = state.getRoundsRemaining();
        prevResult = state.getResult();

        updateMap();
    }

    private void updateMap() {
        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                cells[y][x].setText("");
                cells[y][x].setBackground(Color.WHITE);
            }
        }

        putCell(1, 1, "O1");
        putCell(3, 2, "O2");
        putCell(4, 4, "O3");

        cells[1][1].setBackground(OBJ_COLOR);
        cells[2][3].setBackground(OBJ_COLOR);
        cells[4][4].setBackground(OBJ_COLOR);

        int p1x = state.getPlayerX(TimedRaidState.PlayerId.P1);
        int p1y = state.getPlayerY(TimedRaidState.PlayerId.P1);
        int p2x = state.getPlayerX(TimedRaidState.PlayerId.P2);
        int p2y = state.getPlayerY(TimedRaidState.PlayerId.P2);

        putCell(p1x, p1y, mergeText(cells[p1y][p1x].getText(), "P1"));
        putCell(p2x, p2y, mergeText(cells[p2y][p2x].getText(), "P2"));

        if (state.getCurrentPlayer() == TimedRaidState.PlayerId.P1) {
            cells[p1y][p1x].setBackground(ACTIVE_COLOR);
        } else {
            cells[p2y][p2x].setBackground(ACTIVE_COLOR);
        }
    }

    private void putCell(int x, int y, String text) {
        if (x < 0 || x >= W || y < 0 || y >= H) return;
        cells[y][x].setText(text);
    }

    private String mergeText(String existing, String add) {
        if (existing == null || existing.isBlank()) return add;
        if (existing.contains(add)) return existing;
        return existing + "\n" + add;
    }

    private String tileAt(TimedRaidState.PlayerId player) {
        int idx = state.getObjectiveIndexAtPlayer(player);
        if (idx == 0) return "Objective O1";
        if (idx == 1) return "Objective O2";
        if (idx == 2) return "Objective O3";

        int x = state.getPlayerX(player);
        int y = state.getPlayerY(player);
        return "Empty (" + x + "," + y + ")";
    }
}