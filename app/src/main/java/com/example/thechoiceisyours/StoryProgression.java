package com.example.thechoiceisyours;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.widget.FrameLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.android_viewer.util.DefaultFragment;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class StoryProgression extends FragmentActivity implements ViewerListener {
    private static final int CONTENT_VIEW_ID = 10101010;
    private DefaultFragment fragment;
    private Graph graph;
    public static int nodeCount = 0;
    protected boolean loop = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frame = new FrameLayout(this);
        frame.setId(CONTENT_VIEW_ID);
        setContentView(frame, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        graph = new SingleGraph("TestGraph");
        graph.setAttribute("ui.antialias");

        try {
            // Load the text file from the assets directory
            AssetManager assetManager = getAssets();
            InputStream inputStream = assetManager.open("vol2Tree.txt");

            // Parse the text file to create the graph
            Scanner fileScanner = new Scanner(inputStream);
            int numNodes = Integer.parseInt(fileScanner.nextLine());
            int numEdges = Integer.parseInt(fileScanner.nextLine());

            addVertices(numNodes);

            while (fileScanner.hasNextLine()) {
                String vertices = fileScanner.nextLine();
                if (vertices.equals(""))
                    continue;   // 5compsG.txt does not have any edges
                String[] connection = vertices.split(" ");
                String vertex1 = connection[0];
                String vertex2 = connection[1];
                connectVertices(vertex1, vertex2);
                numEdges++;
            }
            fileScanner.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        display(savedInstanceState, graph, true);
    }

    /**
     * In onStart, the AndroidViewer is already created
     */
    @Override
    protected void onStart() {
        super.onStart();

        ViewerPipe pipe = fragment.getViewer().newViewerPipe();
        pipe.addViewerListener(this);

        String styleSheet = "graph {" +
                "fill-mode: gradient-radial;" +
                "fill-color: white, gray;" +
                "padding: 60px;" +
                "}" +
                "node {" +
                "shape: circle;" +
                "size: 48,48;" +
                "fill-mode: gradient-vertical;" +
                "fill-color: red;" +
                "stroke-mode: plain;" +
                "stroke-color: gray;" +
                "stroke-width: 9px;" +
                "shadow-mode: plain;" +
                "shadow-width: 0px;" +
                "shadow-offset: 15px, -15px;" +
                "shadow-color: rgba(0,0,0,100);" +
                //"text-visibility-mode: zoom-range;" +
                "text-size: 20px;" +
                //"text-visibility: 0, 0.9;" +
                "}" +
                "node:clicked {" +
                "stroke-mode: plain;" +
                "stroke-color: red;" +
                "}" +
                "node:selected {" +
                "stroke-mode: plain;" +
                "stroke-width: 4px;" +
                "stroke-color: blue;" +
                "}" +
                "edge {" +
                "size: 5px;" +
                "arrow-size: 20px, 15px;" +
                "shape: cubic-curve;" +
                "fill-color: rgb(128,128,128);" +
                "fill-mode: plain;" +
                "stroke-mode: plain;" +
                "stroke-color: rgb(80,80,80);" +
                "stroke-width: 9px;" +
                "shadow-mode: none;" +
                "shadow-color: rgba(0,0,0,50);" +
                "shadow-offset: 15px, -15px;" +
                "shadow-width: 0px;" +
                "arrow-shape: diamond;" +
                "}";
        graph.setAttribute("ui.stylesheet", styleSheet);

        new Thread( () ->  {
            while (loop) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                pipe.pump();
            }

            System.exit(0);
        }).start();
    }

    public void addVertices(int numberNodes) throws IOException {
        AssetManager assetManager = getAssets();
        InputStream inputStream = assetManager.open("vol2NodeNames.txt");
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String[] nodeNames = new String[numberNodes];
        // Parse the text file to create the graph
        String line;
        for (int i = 0; i < numberNodes; i++) {
            graph.addNode(String.valueOf(i));
            line = bufferedReader.readLine();
            nodeNames[i] = line;
        }
        int name = 0;
        for (Node n : graph) {
            n.setAttribute("ui.label", String.valueOf(nodeNames[name]));
            name++;
            nodeCount++;
        }
    }

    public void connectVertices(String vertex1, String vertex2) {
        String edgeID = vertex1 + vertex2;
        graph.addEdge(edgeID, vertex1, vertex2);
    }

    public void display(Bundle savedInstanceState, Graph graph, boolean autoLayout) {
        if (savedInstanceState == null) {
            FragmentManager fm = getSupportFragmentManager();

            // find fragment or create him
            fragment = (DefaultFragment) fm.findFragmentByTag("fragment_tag");
            if (null == fragment) {
                fragment = new DefaultFragment();
                fragment.init(graph, autoLayout);
            }

            // Add the fragment in the layout and commit
            FragmentTransaction ft = fm.beginTransaction() ;
            ft.add(CONTENT_VIEW_ID, fragment).commit();
        }
    }

    public void buttonPushed(String id) {
    }

    public void buttonReleased(String id) {
    }

    public void mouseOver(String id) {
    }

    public void mouseLeft(String id) {
    }

    public void viewClosed(String viewName) {
        loop = false;
    }
}