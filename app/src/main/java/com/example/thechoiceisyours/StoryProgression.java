package com.example.thechoiceisyours;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
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
        // Set up View for GraphStream
        FrameLayout frame = new FrameLayout(this);
        frame.setId(CONTENT_VIEW_ID);
        setContentView(frame, new FrameLayout.LayoutParams( // For GraphStream View
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        // Add a button to return to previous screen
        Button button = new Button(this);
        button.setBackgroundResource(R.drawable.rounded_button);
        //button.setBackgroundColor(getResources().getColor(R.color.brown_tan));
        button.setPadding(25, 25, 25, 25);
        button.setText(getText(R.string.story_progress_btn));
        button.setTextColor(getResources().getColor(R.color.white));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams( // For Button
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        params.setMargins(50, 50, 50, 50);

        frame.addView(button, params);      // Add button to the view

        button.setOnClickListener(v -> {    // Button Functionality
            Intent storyProgressionToBookCoverIntent = new Intent(String.valueOf(BookCover.class));
            startActivity(storyProgressionToBookCoverIntent);
        });

        // Generate Graph
        graph = new SingleGraph("TestGraph");
        graph.setAttribute("ui.antialias");

        try {
            // Load the story tree from the assets directory
            AssetManager assetManager = getAssets();
            InputStream inputStream = assetManager.open("vol2_files/vol2Tree.txt");

            // Read the text file to create the graph
            Scanner fileScanner = new Scanner(inputStream);
            int numNodes = Integer.parseInt(fileScanner.nextLine());

            addVertices(numNodes);  // See method below

            while (fileScanner.hasNextLine()) {
                String vertices = fileScanner.nextLine();
                if (vertices.equals(""))
                    continue;   // In case the vertex does not have any edges
                String[] connection = vertices.split(" ");
                String vertex1 = connection[0];
                String vertex2 = connection[1];
                connectVertices(vertex1, vertex2);  // See method below
            }
            fileScanner.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        display(savedInstanceState, graph, true);
    }

    // Refer to: https://github.com/caturananta/AndroidGraphView
    // From post: https://stackoverflow.com/questions/66806508/graphstream-android-not-able-to-display-graph-in-fragment
    /** In onStart, the AndroidViewer is already created */
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
                "fill-color: gray;" +
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
    /** End of https://github.com/caturananta/AndroidGraphView */

    public void addVertices(int numberNodes) throws IOException {
        // For Vertex/Node label, determine the Node ID from a NodeNames.txt asset
        // Open asset file
        AssetManager assetManager = getAssets();
        InputStream inputStream = assetManager.open("vol2_files/vol2NodeNames.txt");
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        // Retain the node IDs in an array
        String[] nodeNames = new String[numberNodes];
        String line;
        for (int i = 0; i < numberNodes; i++) {
            graph.addNode(String.valueOf(i));
            line = bufferedReader.readLine();
            nodeNames[i] = line;
        }

        // Set the ID label attribute for the nodes from the ID array
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

    // Refer to GraphStream's GitHub ReadMe:
    /** https://github.com/graphstream/gs-ui-android */
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
    /* End of https://github.com/graphstream/gs-ui-android */

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