package administrator.gui.overview.all;

import administrator.dao.DBObject;
import administrator.dbHandler.IDBAHandler;
import administrator.dbHandler.table.ITableHandler;
import administrator.gui.overview.DBObjectRowPanel;
import administrator.gui.overview.viewDetails.IShowDetails;
import common.gui.table.ITablePanel;
import common.gui.table.TablePanel;
import common.gui.table.cells.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;

/**
 * administrator.gui
 * Created by NhatLinh - 19127652
 * Date 2/22/2022 - 3:06 PM
 * Description: ...
 */
class UpdateManually extends Thread {
    private final IDBAHandler dbHandler;
    private final Consumer<List<DBObject>> onShow;
    public UpdateManually(IDBAHandler dbHandler, Consumer<List<DBObject>> onShow)
    {
        this.dbHandler = dbHandler;
        this.onShow = onShow;
    }

    @Override
    public void run() {
        try
        {
            List<DBObject> objs = dbHandler.getObjects();
            onShow.accept(objs);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
class SyncDbObjectThread extends UpdateManually {

    private boolean shouldRun = true;
    private boolean shouldPause = true;
    private long delay;

    public void end()
    {
        shouldRun = false;
    }

    public void setPause(boolean pause) {
        shouldPause = pause;
    }

    public void setDelay(long delay)
    {
        if (delay >= 0)
            this.delay = delay;
    }


    public SyncDbObjectThread(IDBAHandler dbHandler, Consumer<List<DBObject>> onShow, long delay)
    {
        super(dbHandler, onShow);
        this.delay = delay;
    }

    @Override
    public void run() {
        try
        {
            while (shouldRun)
            {
                if (shouldPause)
                {
                    synchronized (this)
                    {
                        wait();
                    }
                }
                if (shouldRun && !shouldPause)
                {
                    System.out.println("UPDATE");
                    super.run();
                    synchronized (this)
                    {
                        wait(delay);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}

public abstract class OverviewPanel extends JPanel {
    //protected final JButton reloadButton = new JButton("Reload information");
    protected final JTextField searchFiled = new JTextField();
    protected final ITablePanel tablePanel;
    protected final IShowDetails viewInsideObjectsFrame;
    protected final JCheckBox updateCheckBox = new JCheckBox("Update constantly");
    protected final JSpinner selectDelay;
    private final SyncDbObjectThread syncThread;
    protected final IDBAHandler handler;
    protected final ITableHandler permissionHandler;

    public OverviewPanel(IDBAHandler handler, ICell[] titles, IShowDetails viewInsideObjects, ITableHandler permissionHandler)
    {
        super(new BorderLayout());
        this.handler = handler;
        this.permissionHandler = permissionHandler;
        tablePanel = new TablePanel(titles);
        this.viewInsideObjectsFrame = viewInsideObjects;
        searchFiled.setToolTipText("Input to search");
        this.searchFiled.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                getHint();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                getHint();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                getHint();
            }
        });
        searchFiled.setBorder(BorderFactory.createTitledBorder("Search"));

        syncThread = new SyncDbObjectThread(handler, this::showObjects, 5000);
        JPanel delaySection = new JPanel(new BorderLayout());
        SpinnerModel spinnerModel = new SpinnerNumberModel(5, 5, 60, 1);
        selectDelay = new JSpinner(spinnerModel);
        selectDelay.setBorder(BorderFactory.createTitledBorder("Update frequency (second)"));
        JFormattedTextField spinnerField = ((JSpinner.DefaultEditor)selectDelay.getEditor()).getTextField();
        spinnerField.setEditable(false);
        spinnerField.setHorizontalAlignment(JTextField.LEFT);
        selectDelay.addChangeListener(e -> {
            syncThread.setDelay((Integer)selectDelay.getValue() * 1000);
            System.out.println(selectDelay.getValue());
        });
        updateCheckBox.setSelected(false);
        updateCheckBox.addActionListener(e -> {
            if (updateCheckBox.isSelected())
            {
                syncThread.setPause(false);
                synchronized (syncThread)
                {
                    syncThread.notify();
                }
            }
            else
            {
                syncThread.setPause(true);
            }
        });
        JButton updateManually = new JButton("Update manually");
        updateManually.addActionListener(e -> {
            UpdateManually update = new UpdateManually(handler, this::showObjects);
            update.start();
            try {
                update.join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        });

        delaySection.add(updateCheckBox, BorderLayout.LINE_START);
        delaySection.add(selectDelay, BorderLayout.CENTER);
        delaySection.add(updateManually, BorderLayout.LINE_END);

        JPanel inputSection = new JPanel(new GridLayout(2, 1));
        inputSection.add(delaySection);
        inputSection.add(searchFiled);

        add(inputSection, BorderLayout.PAGE_START);
        add(tablePanel.getAddComponent(), BorderLayout.CENTER);
        //Update the first time
        UpdateManually firstTime = new UpdateManually(handler, this::showObjects);
        firstTime.start();
        try {
            firstTime.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        syncThread.start();
    }

    public void dispose()
    {
        try {
            viewInsideObjectsFrame.dispose();
            syncThread.setPause(false);
            syncThread.end();
            synchronized (syncThread)
            {
                syncThread.notify();
            }
            syncThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void showObjects(java.util.List<DBObject> objs) {
        if (objs != null && !objs.isEmpty())
        {
            try {
                tablePanel.clearRows();
                for (DBObject obj : objs)
                {
                    ButtonCell view = new ButtonCell(obj.getShown());
                    DBObjectRowPanel objRow = new DBObjectRowPanel(obj, getCell(obj, view));
                    view.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            viewInsideObjectsFrame.setObjects(obj.getName(), handler.getInsideObjects(obj.getName()));
                            viewInsideObjectsFrame.setVisible(true);
                        }
                    });
                    //objRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, objRow.getPreferredSize().height));
                    tablePanel.addRow(objRow);
                }

                SwingUtilities.invokeLater(() -> {
                    getHint();
                    tablePanel.updateUI();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected abstract ICell[] getCell(DBObject obj, ButtonCell view);

    protected void dropObject(String name)
    {
        if (handler.dropObject(name))
        {
            tablePanel.deleteRow(name);
            SwingUtilities.invokeLater(() -> {
                tablePanel.updateUI();
                JOptionPane.showMessageDialog(this, "Drop the object successfully");
            });
        }
        else
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Fail to drop the object"));
    }

    protected void getHint()
    {
        String text = searchFiled.getText();
        if (text.isEmpty())
            tablePanel.showRows();
        else
            tablePanel.hideRows(text);
    }
}
