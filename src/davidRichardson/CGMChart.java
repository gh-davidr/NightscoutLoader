package davidRichardson;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.BorderLayout;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JDialog;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.ChartUtilities; 

public class CGMChart  extends JDialog 
{
	private ArrayList <DBResultEntry>         m_DBResultEntries;
	private JFreeChart                        m_JFreeChart;
	private DefaultCategoryDataset            m_DataSet;

	CGMChart(ArrayList <DBResultEntry> resultEntries)
	{
		m_DBResultEntries = resultEntries;

		setBounds(100, 100, 770, 550);

		m_DataSet = createCGMDataset();

		m_JFreeChart = ChartFactory.createLineChart(
				"CGM ","Date",
				"CGM Values",
				m_DataSet,
				PlotOrientation.VERTICAL,
				true,true,false);	

		ChartPanel chartPanel = new ChartPanel( m_JFreeChart );

		JPanel contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		contentPane.add(chartPanel);

	}

	CGMChart(ArrayList <DBResultEntry> resultEntries,
			Date startDate, Date endDate )
	{
		m_DBResultEntries = resultEntries;

		setBounds(100, 100, 770, 550);
		m_DataSet = createCGMDataset(startDate, endDate);

		m_JFreeChart = ChartFactory.createLineChart(
				"CGM ","Date",
				"CGM Values",
				m_DataSet,
				PlotOrientation.VERTICAL,
				true,true,false);	

		ChartPanel chartPanel = new ChartPanel( m_JFreeChart );

		JPanel contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		contentPane.add(chartPanel);

	}


	public ByteArrayOutputStream getByteStream()
	{
		ByteArrayOutputStream result = null;

		int width=640; /* Width of the chart */
		int height=480; /* Height of the chart */
		float quality=1; /* Quality factor */

		try {
			if (m_DataSet != null && m_DataSet.getRowCount() > 0)
			{
				result = new ByteArrayOutputStream();
 				ChartUtilities.writeChartAsJPEG(result,quality,m_JFreeChart,width,height);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;		
	}

	private DefaultCategoryDataset createCGMDataset()
	{
		DefaultCategoryDataset dataset = null;

		for (DBResultEntry c : this.m_DBResultEntries)
		{
			if (dataset == null)
			{
				dataset = new DefaultCategoryDataset( );
			}

			Double sgv = c.getM_SGV();
			String dte = c.getM_DateString();

			if (sgv != null)
			{
				dataset.addValue(sgv, "CGM Value", dte);
			}

		}
		return dataset;
	}


	private DefaultCategoryDataset createCGMDataset(Date startDate, Date endDate )
	{
		DefaultCategoryDataset dataset = null;

		for (DBResultEntry c : this.m_DBResultEntries)
		{
			if (CommonUtils.isTimeBetween(startDate, endDate, c.getM_UTCDate()))
			{
				if (dataset == null)
				{
					dataset = new DefaultCategoryDataset( );
				}

				Double sgv = c.getM_SGV();
				String dte = c.getM_DateString();

				if (sgv != null)
				{
					dataset.addValue(sgv, "CGM Value", dte);
				}
			}
		}
		return dataset;
	}



}
