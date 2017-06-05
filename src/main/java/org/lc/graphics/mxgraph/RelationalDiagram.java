package org.lc.graphics.mxgraph;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.apache.commons.codec.binary.Base64;
import org.lc.model.CorrGraphLink;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.layout.mxEdgeLabelLayout;
import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.layout.mxPartitionLayout;
import com.mxgraph.layout.mxStackLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxPerimeter;

public class RelationalDiagram
{

	public static mxGraphComponent draw(String cls, String[] attr) {
		mxGraph graph = new mxGraph();
		Object parent = graph.getDefaultParent();

		graph.getModel().beginUpdate();
		try
		{
			Map<String, Object> style = graph.getStylesheet().getDefaultVertexStyle();
			style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
			style.put(mxConstants.STYLE_PERIMETER, mxPerimeter.EllipsePerimeter);
			style.put(mxConstants.STYLE_GRADIENTCOLOR, "white");
			style.put(mxConstants.STYLE_FONTSIZE, "10");
			graph.setGridSize(40);
			mxFastOrganicLayout layout = new mxFastOrganicLayout(graph);
			layout.setForceConstant(80);
			int w = 30, h = 30;
			Object vt = graph.insertVertex(parent, null, cls, 0, 0, w, h);
			for (int i=0; i<attr.length; i++) {
				Object vp = graph.insertVertex(parent, null, attr[i], 0, 0, w, h);
				graph.insertEdge(parent, null,attr[i]+"=>"+cls, vp, vt);
			}			
			layout.execute(parent);
		}
		finally
		{
			graph.getModel().endUpdate();
			try {
				BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1.0, new Color(255, 255, 240, 255), true, null);
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				//ImageIO.write(image, "PNG", new File("C:\\Temp\\graph.png"));
				ImageIO.write(image, "PNG", os);
				System.out.println(Base64.encodeBase64String(os.toByteArray()));
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}

		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		return graphComponent;
	}
	
	public static String drawBase64(String cls, List<String> attr, String... path) {
		mxGraph graph = new mxGraph();
		Object parent = graph.getDefaultParent();

		graph.getModel().beginUpdate();
		try
		{
			Map<String, Object> style = graph.getStylesheet().getDefaultVertexStyle();
			style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
			style.put(mxConstants.STYLE_PERIMETER, mxPerimeter.EllipsePerimeter);
			style.put(mxConstants.STYLE_GRADIENTCOLOR, "white");
			style.put(mxConstants.STYLE_FONTSIZE, "10");
			graph.setGridSize(40);
			mxFastOrganicLayout layout = new mxFastOrganicLayout(graph);
			layout.setForceConstant(80);
			int w = 30, h = 30;
			Object vt = graph.insertVertex(parent, null, cls, 0, 0, w, h);
			for (String a : attr) {
				Object vp = graph.insertVertex(parent, null, a, 0, 0, w, h);
				graph.insertEdge(parent, null, a+"=>"+cls, vp, vt);
			}			
			layout.execute(parent);
		}
		finally
		{
			graph.getModel().endUpdate();
			try {
				BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1.0, new Color(255, 255, 240, 255), true, null);
				if (path!=null && path.length>0) {
					String realpath = path[0];
					ImageIO.write(image, "PNG", new File(realpath));
					return realpath;
				} else {
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					ImageIO.write(image, "PNG", os);
					return Base64.encodeBase64String(os.toByteArray());
				}
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		return "";
	}	

	public static String drawBase64(List<CorrGraphLink> links, String... path) {
		mxGraph graph = new mxGraph();
		Object parent = graph.getDefaultParent();
		String cls = "";
		if (links!=null) {
			for (CorrGraphLink link : links) {
				cls = link.getTarget();
				break;
			}
		}
		graph.getModel().beginUpdate();
		try
		{
			Map<String, Object> style = graph.getStylesheet().getDefaultVertexStyle();
			style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
			style.put(mxConstants.STYLE_PERIMETER, mxPerimeter.EllipsePerimeter);
			style.put(mxConstants.STYLE_GRADIENTCOLOR, "white");
			style.put(mxConstants.STYLE_FONTSIZE, "10");
			graph.setGridSize(40);
			mxFastOrganicLayout layout = new mxFastOrganicLayout(graph);
			layout.setForceConstant(80);
			int w = 30, h = 30;
			Object vt = graph.insertVertex(parent, null, cls, 0, 0, w, h);
			for (CorrGraphLink link : links) {
				Object vp = graph.insertVertex(parent, null, link.getSource(), 0, 0, w, h);
				graph.insertEdge(parent, null, Math.round(link.getCorr()*100)+"%", vp, vt);
			}			
			layout.execute(parent);
		}
		finally
		{
			graph.getModel().endUpdate();
			try {
				BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1.0, new Color(255, 255, 240, 255), true, null);
				if (path!=null && path.length>0) {
					String realpath = path[0];
					ImageIO.write(image, "PNG", new File(realpath));
					return realpath;
				} else {
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					ImageIO.write(image, "PNG", os);
					return Base64.encodeBase64String(os.toByteArray());
				}
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		return "";
	}	
	
	public RelationalDiagram()
	{

	}
	
	public static void main(String[] args)
	{
//		JFrame frame = new JFrame();
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.setSize(400, 320);
//		mxGraphComponent graphComponent = RelationalDiagram.draw("IBM", new String[]{"^GSPC", "^NDX"}); 
//		frame.getContentPane().add(graphComponent);
//		frame.setVisible(true);
		List<CorrGraphLink> links = new ArrayList<CorrGraphLink>();
		links.add(new CorrGraphLink("^GSPC", "GOOG", .32));
		links.add(new CorrGraphLink("^NDX", "GOOG", .39));
		links.add(new CorrGraphLink("^DJC", "GOOG", -.2));
		RelationalDiagram.drawBase64(links, "c:\\temp\\tmp.png");		
	}

}
