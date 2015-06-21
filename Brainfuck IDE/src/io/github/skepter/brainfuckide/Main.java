package io.github.skepter.brainfuckide;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

/**
 * Brainfuck IDE designed to run and debug Brainfuck code Extra credit to Fabian
 * M for BrainfuckEngine.java https://github.com/fabianm
 * 
 * @author Skepter
 *
 */
public class Main {

	private JFrame mainFrame;
	public static JTextArea workspace;
	private JTextArea memoryOutput;
	private JTextField cellCount;
	private static JLabel statusLabel;
	public static JTextArea output;
	private final static int DEFAULT_MEMORY = 384;

	private static int memory = DEFAULT_MEMORY;
	private JTextField inputField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (final Exception e) {
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.mainFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		/* Basic frame setup */

		mainFrame = new JFrame();
		mainFrame.setTitle("Brainfuck IDE");
		mainFrame.setBounds(100, 100, 1100, 700);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		/* Status bar (This comes first because it has info that gets updates) */

		JPanel statusBar = new JPanel();
		mainFrame.getContentPane().add(statusBar, BorderLayout.SOUTH);
		statusBar.setLayout(new BorderLayout(0, 0));

		statusLabel = new JLabel("Memory: 384");
		statusBar.add(statusLabel, BorderLayout.NORTH);

		/* Main viewport */

		JPanel mainPanel = new JPanel();
		mainFrame.getContentPane().add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new BorderLayout(0, 0));

		memoryOutput = new JTextArea();
		memoryOutput.setEditable(false);

		/* Split panes */
		{
			{
				JSplitPane splitPane = new JSplitPane();
				mainPanel.add(splitPane, BorderLayout.CENTER);
				splitPane.setDividerLocation(700);

				/* Left split pane - workspace + output */
				{
					JSplitPane workspaceOutputPane = new JSplitPane();
					JScrollPane leftScrollPane = new JScrollPane();
					leftScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
					splitPane.setLeftComponent(workspaceOutputPane);
					workspace = new JTextArea();
					workspace.setLineWrap(true);
					workspace.setText("Hello World!\n++++++++++[>+++++++>++++++++++>+++>+<<<<-]>++.>+.+++++++..+++.>++.<<+++++++++++++++.>.+++.------.--------.>+.>.");
					leftScrollPane.setViewportView(workspace);

					workspaceOutputPane.setLeftComponent(leftScrollPane);
					workspaceOutputPane.setOrientation(JSplitPane.VERTICAL_SPLIT);

					workspaceOutputPane.setDividerLocation(400);
					JScrollPane outputPane = new JScrollPane();
					outputPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
					output = new JTextArea();
					outputPane.setViewportView(output);

					workspaceOutputPane.setRightComponent(outputPane);

				}

				/* Right split pane - controls + memory output */
				{

					JSplitPane secondSplitPane = new JSplitPane();
					secondSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
					secondSplitPane.setDividerLocation(150);
					splitPane.setRightComponent(secondSplitPane);

					JPanel devPanel = new JPanel();
					devPanel.setBackground(new Color(135, 206, 250));
					devPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Controls", TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 0, 0)));
					secondSplitPane.setLeftComponent(devPanel);

					JButton runButton = new JButton("Run!");
					runButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							new BrainfuckIntegrator(new BrainfuckEngine(memory, System.out, new JTextFieldInputStream(inputField)), workspace.getText(), memoryOutput);
						}
					});

					JButton resetButton = new JButton("Reset");
					resetButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							memoryOutput.setText("");
							output.setText("");
						}
					});

					/* Set memory section */

					JLabel setMemoryLabel = new JLabel("Set memory (Cells)");
					cellCount = new JTextField();
					cellCount.addKeyListener(new KeyListener() {

						@Override
						public void keyPressed(KeyEvent arg0) {
						}

						@Override
						public void keyReleased(KeyEvent arg0) {
							try {
								Integer.parseInt(cellCount.getText());
								memory = Integer.parseInt(cellCount.getText());
							} catch (Exception e) {
								memory = DEFAULT_MEMORY;
							}
							statusLabel.setText("Memory: " + memory);
						}

						@Override
						public void keyTyped(KeyEvent arg0) {
						}

					});
					cellCount.setColumns(10);

					/* Group layout for controls */

					inputField = new JTextField();
					inputField.setColumns(10);

					JLabel inputLabel = new JLabel("Input:");

					JButton btnNewButton = new JButton("Format");
					btnNewButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							new BrainfuckFormatter().format();
						}
					});

					GroupLayout gl_devPanel = new GroupLayout(devPanel);
					gl_devPanel.setHorizontalGroup(gl_devPanel.createParallelGroup(Alignment.LEADING).addGroup(
							gl_devPanel
									.createSequentialGroup()
									.addContainerGap()
									.addGroup(
											gl_devPanel.createParallelGroup(Alignment.LEADING).addComponent(resetButton, GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE)
													.addComponent(btnNewButton, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE).addComponent(runButton, GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE)).addGap(40)
									.addGroup(gl_devPanel.createParallelGroup(Alignment.TRAILING).addComponent(setMemoryLabel).addComponent(inputLabel, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE))
									.addPreferredGap(ComponentPlacement.UNRELATED).addGroup(gl_devPanel.createParallelGroup(Alignment.TRAILING, false).addComponent(cellCount).addComponent(inputField, GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE))
									.addContainerGap()));
					gl_devPanel.setVerticalGroup(gl_devPanel.createParallelGroup(Alignment.LEADING).addGroup(
							gl_devPanel
									.createSequentialGroup()
									.addContainerGap()
									.addGroup(
											gl_devPanel.createParallelGroup(Alignment.BASELINE).addComponent(runButton).addComponent(cellCount, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
													.addComponent(setMemoryLabel))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(
											gl_devPanel.createParallelGroup(Alignment.BASELINE).addComponent(resetButton).addComponent(inputField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
													.addComponent(inputLabel)).addPreferredGap(ComponentPlacement.RELATED).addComponent(btnNewButton).addContainerGap(34, Short.MAX_VALUE)));
					devPanel.setLayout(gl_devPanel);

					/* Set output */

					secondSplitPane.setRightComponent(memoryOutput);

				}
			}
		}

	}

	public static void setStatusLabel(int pointer, boolean input) {
		if (input) {
			statusLabel.setText("Memory: " + memory + ", Pointer: " + pointer);
		} else {
			statusLabel.setText("Memory: " + memory + ", Pointer: " + pointer + ", Awaiting an input......");
		}
	}
}
