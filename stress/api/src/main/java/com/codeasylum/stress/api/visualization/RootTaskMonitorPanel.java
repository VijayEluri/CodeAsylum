/*
 * Copyright (c) 2007, 2008, 2009, 2010, 2011, 10212, 2013 David Berkman
 * 
 * This file is part of the CodeAsylum Code Project.
 * 
 * The CodeAsylum Code Project is free software, you can redistribute
 * it and/or modify it under the terms of GNU Affero General Public
 * License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * The CodeAsylum Code Project is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the the GNU Affero General Public
 * License, along with The CodeAsylum Code Project. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 * Additional permission under the GNU Affero GPL version 3 section 7
 * ------------------------------------------------------------------
 * If you modify this Program, or any covered work, by linking or
 * combining it with other code, such other code is not for that reason
 * alone subject to any of the requirements of the GNU Affero GPL
 * version 3.
 */
package com.codeasylum.stress.api.visualization;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import com.codeasylum.stress.api.ExchangeEvent;
import com.codeasylum.stress.api.ExchangeListener;
import com.codeasylum.stress.api.TestControl;
import com.codeasylum.stress.api.TestExecutor;
import com.codeasylum.stress.api.TestExecutorEvent;
import com.codeasylum.stress.api.TestExecutorListener;
import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.axis.AAxis;
import info.monitorenter.gui.chart.axis.AxisLinear;
import info.monitorenter.gui.chart.labelformatters.LabelFormatterDate;
import info.monitorenter.gui.chart.labelformatters.LabelFormatterNumber;
import info.monitorenter.gui.chart.traces.painters.TracePainterVerticalBar;
import org.smallmind.swing.ColorUtilities;
import org.smallmind.swing.Colorizer;
import org.smallmind.swing.spinner.DefaultSpinnerRenderer;
import org.smallmind.swing.spinner.IntegerSpinnerEditor;
import org.smallmind.swing.spinner.IntegerSpinnerModel;
import org.smallmind.swing.spinner.Spinner;

public class RootTaskMonitorPanel extends JPanel implements ChangeListener, ActionListener, ListSelectionListener, TestExecutorListener, ExchangeListener, ComponentListener {

  private static final ImageIcon RUN_ICON = new ImageIcon(ClassLoader.getSystemResource("com/codeasylum/stress/api/run_16.png"));
  private static final ImageIcon CANCEL_ICON = new ImageIcon(ClassLoader.getSystemResource("com/codeasylum/stress/api/cancel_16.png"));
  private static final int SCROLL_BAR_WIDTH = (Integer)UIManager.getDefaults().get("ScrollBar.width");

  private final HashMap<String, ITrace2D> responseTimeTraceMap = new HashMap<String, ITrace2D>();
  private final HashMap<String, ITrace2D> requestCountTraceMap = new HashMap<String, ITrace2D>();

  private JFrame parentFrame;
  private TestExecutor testExecutor;
  private Colorizer colorizer = new Colorizer();
  private MonitorStatistics monitorStatistics;
  private Chart2D responseTimeChart;
  private Chart2D requestCountChart;
  private Chart2D failureCountChart;
  private PerSecondTimedTrace failureTrace;
  private PerSecondTimeRangePolicy responseTimeRangePolicy;
  private PerSecondTimeRangePolicy requestCountRangePolicy;
  private PerSecondTimeRangePolicy failureCountRangePolicy;
  private JList legendList;
  private MonitorLegendListModel monitorLegendListModel;
  private IntegerSpinnerModel durationSpinnerModel;
  private JButton runButton;
  private JButton cancelButton;
  private JTextField averageResponseTimeTextField;
  private JTextField averageRequestCountTextField;
  private JTextField averageFailureCountTextField;
  private JTextField totalFailureCountTextField;
  private JTextField percentageFailureCountTextField;

  public RootTaskMonitorPanel (JFrame parentFrame, TestExecutor testExecutor)
    throws RemoteException {

    JPanel innerPanel;
    GroupLayout groupLayout;
    AAxis responseTimeYAxis;
    AAxis requestCountYAxis;
    AAxis failureCountYAxis;
    JScrollPane legendScrollPane;
    Spinner durationSpinner;
    JLabel durationLabel;
    JLabel responseTimeLabel;
    JLabel requestCountLabel;
    JLabel failureCountLabel;
    JLabel averageResponseTimeLabel;
    JLabel averageRequestCountLabel;
    JLabel averageFailureCountLabel;
    JLabel totalFailureCountLabel;
    JLabel percentageFailureCountLabel;
    int checkboxHeight;

    this.parentFrame = parentFrame;
    this.testExecutor = testExecutor;

    setLayout(new GridLayout(1, 1));
    add(new JScrollPane(innerPanel = new JPanel(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
    innerPanel.setLayout(groupLayout = new GroupLayout(innerPanel));

    durationLabel = new JLabel("Monitor Time (minutes):");

    responseTimeLabel = new JLabel("Response Time (ms/s)", JLabel.CENTER);
    responseTimeLabel.setOpaque(true);
    responseTimeLabel.setBackground(ColorUtilities.INVERSE_TEXT_COLOR);
    responseTimeLabel.setForeground(ColorUtilities.TEXT_COLOR);
    responseTimeLabel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, ColorUtilities.INVERSE_TEXT_COLOR));

    requestCountLabel = new JLabel("Number of Requests (#/s)", JLabel.CENTER);
    requestCountLabel.setOpaque(true);
    requestCountLabel.setBackground(ColorUtilities.INVERSE_TEXT_COLOR);
    requestCountLabel.setForeground(ColorUtilities.TEXT_COLOR);
    requestCountLabel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, ColorUtilities.INVERSE_TEXT_COLOR));

    failureCountLabel = new JLabel("Number of Errors (#/s)", JLabel.CENTER);
    failureCountLabel.setOpaque(true);
    failureCountLabel.setBackground(ColorUtilities.INVERSE_TEXT_COLOR);
    failureCountLabel.setForeground(ColorUtilities.TEXT_COLOR);
    failureCountLabel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, ColorUtilities.INVERSE_TEXT_COLOR));

    averageResponseTimeLabel = new JLabel("Avg. Response Time (ms):");
    averageRequestCountLabel = new JLabel("Avg. Number of Requests (#/s):");
    averageFailureCountLabel = new JLabel("Avg. Number of Failures (#/s):");
    totalFailureCountLabel = new JLabel("Total Number of Failures (#):");
    percentageFailureCountLabel = new JLabel("Failure Rate (%):");

    averageResponseTimeTextField = new JTextField();
    averageResponseTimeTextField.setEditable(false);
    averageResponseTimeTextField.setHorizontalAlignment(JTextField.RIGHT);
    averageResponseTimeTextField.setBackground(ColorUtilities.TEXT_COLOR);

    averageRequestCountTextField = new JTextField();
    averageRequestCountTextField.setEditable(false);
    averageRequestCountTextField.setHorizontalAlignment(JTextField.RIGHT);
    averageRequestCountTextField.setBackground(ColorUtilities.TEXT_COLOR);

    averageFailureCountTextField = new JTextField();
    averageFailureCountTextField.setEditable(false);
    averageFailureCountTextField.setHorizontalAlignment(JTextField.RIGHT);
    averageFailureCountTextField.setBackground(ColorUtilities.TEXT_COLOR);

    totalFailureCountTextField = new JTextField();
    totalFailureCountTextField.setEditable(false);
    totalFailureCountTextField.setHorizontalAlignment(JTextField.RIGHT);
    totalFailureCountTextField.setBackground(ColorUtilities.TEXT_COLOR);

    percentageFailureCountTextField = new JTextField();
    percentageFailureCountTextField.setEditable(false);
    percentageFailureCountTextField.setHorizontalAlignment(JTextField.RIGHT);
    percentageFailureCountTextField.setBackground(ColorUtilities.TEXT_COLOR);

    runButton = new JButton("Run", RUN_ICON);
    runButton.setMargin(new Insets(2, 2, 2, 2));
    runButton.setFocusable(false);
    runButton.setToolTipText("start test run");
    runButton.addActionListener(this);

    cancelButton = new JButton("Cancel", CANCEL_ICON);
    cancelButton.setMargin(new Insets(2, 2, 2, 2));
    cancelButton.setFocusable(false);
    cancelButton.setToolTipText("cancel test run");
    cancelButton.addActionListener(this);

    durationSpinner = new Spinner(durationSpinnerModel = new IntegerSpinnerModel(testExecutor.getTestPlan().getMonitorDuration(), 1, 1, 60), 100);
    durationSpinner.setSpinnerRenderer(new DefaultSpinnerRenderer(SwingConstants.RIGHT));
    durationSpinner.setSpinnerEditor(new IntegerSpinnerEditor(durationSpinnerModel));
    durationSpinnerModel.addChangeListener(this);

    legendList = new JList(monitorLegendListModel = new MonitorLegendListModel());
    legendList.setLayoutOrientation(JList.VERTICAL_WRAP);
    legendList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    legendList.setCellRenderer(new MonitorLegendListCellRenderer());
    legendList.setVisibleRowCount(3);

    legendScrollPane = new JScrollPane(legendList, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    responseTimeChart = new Chart2D();
    responseTimeChart.setFocusable(false);
    responseTimeChart.setPaintLabels(false);
    responseTimeChart.getAxisX().getAxisTitle().setTitle("Time");
    responseTimeChart.getAxisX().setPaintGrid(true);
    responseTimeChart.getAxisX().setFormatter(new LabelFormatterDate(new SimpleDateFormat("HH:mm:ss")));
    responseTimeChart.getAxisX().setRangePolicy(responseTimeRangePolicy = new PerSecondTimeRangePolicy(testExecutor, testExecutor.getTestPlan().getMonitorDuration(), TimeUnit.MINUTES));
    responseTimeChart.removeAxisYLeft(responseTimeChart.getAxisY());
    responseTimeYAxis = new AxisLinear();
    responseTimeYAxis.setPaintGrid(true);
    responseTimeYAxis.getAxisTitle().setTitle("Response Time");
    responseTimeYAxis.setFormatter(new LabelFormatterNumber(new DecimalFormat("0.00")));
    responseTimeChart.addAxisYLeft(responseTimeYAxis);

    requestCountChart = new Chart2D();
    requestCountChart.setFocusable(false);
    requestCountChart.setSynchronizedXStartChart(responseTimeChart);
    requestCountChart.setPaintLabels(false);
    requestCountChart.getAxisX().getAxisTitle().setTitle("Time");
    requestCountChart.getAxisX().setPaintGrid(true);
    requestCountChart.getAxisX().setFormatter(new LabelFormatterDate(new SimpleDateFormat("HH:mm:ss")));
    requestCountChart.getAxisX().setRangePolicy(requestCountRangePolicy = new PerSecondTimeRangePolicy(testExecutor, testExecutor.getTestPlan().getMonitorDuration(), TimeUnit.MINUTES));
    requestCountChart.removeAxisYLeft(requestCountChart.getAxisY());
    requestCountYAxis = new AxisLinear();
    requestCountYAxis.setPaintGrid(true);
    requestCountYAxis.getAxisTitle().setTitle("# of Requests");
    requestCountYAxis.setFormatter(new LabelFormatterNumber(new DecimalFormat("0.00")));
    requestCountChart.addAxisYLeft(requestCountYAxis);

    failureCountChart = new Chart2D();
    failureCountChart.setFocusable(false);
    failureCountChart.setSynchronizedXStartChart(responseTimeChart);
    failureCountChart.setPaintLabels(false);
    failureCountChart.getAxisX().getAxisTitle().setTitle("Time");
    failureCountChart.getAxisX().setPaintGrid(true);
    failureCountChart.getAxisX().setFormatter(new LabelFormatterDate(new SimpleDateFormat("HH:mm:ss")));
    failureCountChart.getAxisX().setRangePolicy(failureCountRangePolicy = new PerSecondTimeRangePolicy(testExecutor, testExecutor.getTestPlan().getMonitorDuration(), TimeUnit.MINUTES));
    failureCountChart.removeAxisYLeft(failureCountChart.getAxisY());
    failureCountYAxis = new AxisLinear();
    failureCountYAxis.setPaintGrid(true);
    failureCountYAxis.getAxisTitle().setTitle("# of Errors");
    failureCountYAxis.setFormatter(new LabelFormatterNumber(new DecimalFormat("0.00")));
    failureCountChart.addAxisYLeft(failureCountYAxis);

    failureTrace = new CountingPerSecondTimedTrace(testExecutor.getTestPlan().getMonitorDuration(), TimeUnit.MINUTES);
    failureTrace.setColor(Color.RED);
    failureTrace.setTracePainter(new TracePainterVerticalBar(failureCountChart));
    failureCountChart.addTrace(failureTrace);

    groupLayout.setAutoCreateContainerGaps(true);
    checkboxHeight = (int)new JCheckBox().getPreferredSize().getHeight();

    groupLayout.setHorizontalGroup(groupLayout.createParallelGroup()
      .addGroup(groupLayout.createSequentialGroup().addComponent(durationLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(durationSpinner, 70, 70, 70).addGap(0, 0, Short.MAX_VALUE).addComponent(runButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
      .addGroup(groupLayout.createSequentialGroup().addComponent(averageResponseTimeLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(averageResponseTimeTextField, 75, 75, 75))
      .addComponent(responseTimeLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE).addComponent(responseTimeChart)
      .addGroup(groupLayout.createSequentialGroup().addComponent(averageRequestCountLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(averageRequestCountTextField, 75, 75, 75))
      .addComponent(requestCountLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE).addComponent(requestCountChart)
      .addComponent(legendScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
      .addGroup(groupLayout.createSequentialGroup().addComponent(averageFailureCountLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(averageFailureCountTextField, 75, 75, 75).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(totalFailureCountLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(totalFailureCountTextField, 75, 75, 75).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(percentageFailureCountLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(percentageFailureCountTextField, 75, 75, 75))
      .addComponent(failureCountLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE).addComponent(failureCountChart));

    groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
      .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(durationLabel).addComponent(durationSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(runButton, 20, 20, 20).addComponent(cancelButton, 20, 20, 20))
      .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
      .addGroup(groupLayout.createParallelGroup().addComponent(averageResponseTimeLabel).addComponent(averageResponseTimeTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
      .addComponent(responseTimeLabel).addComponent(responseTimeChart, 200, 200, Short.MAX_VALUE)
      .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
      .addGroup(groupLayout.createParallelGroup().addComponent(averageRequestCountLabel).addComponent(averageRequestCountTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
      .addComponent(requestCountLabel).addComponent(requestCountChart, 200, 200, Short.MAX_VALUE)
      .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
      .addComponent(legendScrollPane, (checkboxHeight * 3) + SCROLL_BAR_WIDTH, (checkboxHeight * 3) + SCROLL_BAR_WIDTH, (checkboxHeight * 3) + SCROLL_BAR_WIDTH)
      .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
      .addGroup(groupLayout.createParallelGroup().addComponent(averageFailureCountLabel).addComponent(averageFailureCountTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(totalFailureCountLabel).addComponent(totalFailureCountTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(percentageFailureCountLabel).addComponent(percentageFailureCountTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
      .addComponent(failureCountLabel).addComponent(failureCountChart, 150, 150, 150));

    monitorStatistics = new MonitorStatistics();

    addComponentListener(this);
    legendList.addListSelectionListener(this);

    synchronized (this) {
      testExecutor.addTestExecutorListener(this);
      testExecutor.getExchangeTransport().addExchangeListener(this);
      runButton.setEnabled(!testExecutor.isExecuting());
      cancelButton.setEnabled(testExecutor.isExecuting());
    }
  }

  @Override
  public synchronized void executionStarted (TestExecutorEvent testExecutorEvent) {

    runButton.setEnabled(false);
    cancelButton.setEnabled(true);

    for (ITrace2D responseTimeTrace : responseTimeTraceMap.values()) {
      ((PerSecondTimedTrace)responseTimeTrace).clear();
    }
    for (ITrace2D requestCountTrace : requestCountTraceMap.values()) {
      ((PerSecondTimedTrace)requestCountTrace).clear();
    }

    failureTrace.clear();

    monitorStatistics.reset();
    averageResponseTimeTextField.setText("");
    averageRequestCountTextField.setText("");
    averageFailureCountTextField.setText("");
    totalFailureCountTextField.setText("");
    percentageFailureCountTextField.setText("");
  }

  @Override
  public synchronized void executionStopped (TestExecutorEvent testExecutorEvent) {

    runButton.setEnabled(true);
    cancelButton.setEnabled(false);
  }

  @Override
  public synchronized void stateChanged (ChangeEvent changeEvent) {

    testExecutor.getTestPlan().setMonitorDuration((Integer)durationSpinnerModel.getValue());

    for (ITrace2D responseTimeTrace : responseTimeTraceMap.values()) {
      ((PerSecondTimedTrace)responseTimeTrace).setDuration((Integer)durationSpinnerModel.getValue());
    }
    for (ITrace2D requestCountTrace : requestCountTraceMap.values()) {
      ((PerSecondTimedTrace)requestCountTrace).setDuration((Integer)durationSpinnerModel.getValue());
    }

    failureTrace.setDuration((Integer)durationSpinnerModel.getValue());

    responseTimeRangePolicy.setDuration((Integer)durationSpinnerModel.getValue());
    requestCountRangePolicy.setDuration((Integer)durationSpinnerModel.getValue());
    failureCountRangePolicy.setDuration((Integer)durationSpinnerModel.getValue());
  }

  @Override
  public synchronized void actionPerformed (ActionEvent actionEvent) {

    if (actionEvent.getSource() == runButton) {
      TestControl.execute(this, parentFrame, testExecutor);
    }
    else if (actionEvent.getSource() == cancelButton) {
      TestControl.cancel(this, parentFrame, testExecutor);
    }
  }

  @Override
  public synchronized void valueChanged (ListSelectionEvent listSelectionEvent) {

    if (!listSelectionEvent.getValueIsAdjusting()) {

      int selectedIndex;

      if ((selectedIndex = legendList.getSelectedIndex()) >= 0) {

        MonitorLegend legend;
        ITrace2D responseTimeTrace;
        ITrace2D requestCountTrace;

        legend = monitorLegendListModel.flipVisibility(selectedIndex);

        if ((responseTimeTrace = responseTimeTraceMap.get(legend.getName())) != null) {
          responseTimeTrace.setVisible(legend.isVisible());
        }
        if ((requestCountTrace = requestCountTraceMap.get(legend.getName())) != null) {
          requestCountTrace.setVisible(legend.isVisible());
        }

        legendList.clearSelection();
      }
    }
  }

  @Override
  public synchronized void receive (ExchangeEvent exchangeEvent) {

    ITrace2D responseTimeTrace;
    ITrace2D requestCountTrace;
    String taskName;
    long start;
    long elapsedTime;

    if (exchangeEvent.getExchange().isSuccess()) {
      if ((responseTimeTrace = responseTimeTraceMap.get(taskName = exchangeEvent.getExchange().getTaskName())) == null) {

        Color traceColor;

        responseTimeTraceMap.put(taskName, responseTimeTrace = new AveragingPerSecondTimedTrace((Integer)durationSpinnerModel.getValue(), TimeUnit.MINUTES));
        responseTimeTrace.setColor(traceColor = colorizer.getColor(taskName));
        responseTimeTrace.setVisible(monitorLegendListModel.addLegend(new MonitorLegend(taskName, traceColor)).isVisible());

        responseTimeChart.addTrace(responseTimeTrace);
      }
      responseTimeTrace.addPoint(start = exchangeEvent.getExchange().getStartMillis(), elapsedTime = exchangeEvent.getExchange().getStopMillis() - start);

      if ((requestCountTrace = requestCountTraceMap.get(taskName = exchangeEvent.getExchange().getTaskName())) == null) {

        Color traceColor;

        requestCountTraceMap.put(taskName, requestCountTrace = new CountingPerSecondTimedTrace((Integer)durationSpinnerModel.getValue(), TimeUnit.MINUTES));
        requestCountTrace.setColor(traceColor = colorizer.getColor(taskName));
        requestCountTrace.setVisible(monitorLegendListModel.addLegend(new MonitorLegend(taskName, traceColor)).isVisible());

        requestCountChart.addTrace(requestCountTrace);
      }
      requestCountTrace.addPoint(exchangeEvent.getExchange().getStartMillis(), 0);

      monitorStatistics.addExchange(elapsedTime);
    }
    else {
      failureTrace.addPoint(exchangeEvent.getExchange().getStartMillis(), 0);
      monitorStatistics.incFailureCount();
    }

    averageResponseTimeTextField.setText(String.valueOf((int)monitorStatistics.getAverageResponseTime()));
    averageRequestCountTextField.setText(String.valueOf((int)monitorStatistics.getAverageRequestCount()));
    averageFailureCountTextField.setText(String.valueOf((int)monitorStatistics.getAverageFailureCount()));
    totalFailureCountTextField.setText(String.valueOf(monitorStatistics.getTotalFailureCount()));
    percentageFailureCountTextField.setText(String.valueOf((int)monitorStatistics.getFailurePercentage()));
  }

  @Override
  public void componentResized (ComponentEvent componentEvent) {

  }

  @Override
  public void componentMoved (ComponentEvent componentEvent) {

  }

  @Override
  public void componentShown (ComponentEvent componentEvent) {

    responseTimeChart.setRequestedRepaint(true);
    requestCountChart.setRequestedRepaint(true);
    failureCountChart.setRequestedRepaint(true);
  }

  @Override
  public void componentHidden (ComponentEvent componentEvent) {

  }
}