package org.zgnetov;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/start")
public class StartServlet extends HttpServlet {

    public static final int INITIAL_HEALTH = 100;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);

        if (session.getAttribute("visited") == null) {
            session.setAttribute("visited", true);
            session.setAttribute("foodFound", false);
            session.setAttribute("keyFound", false);
            session.setAttribute("toolFound", false);
            session.setAttribute("portalFound", false);
            session.setAttribute("win", false);
            session.setAttribute("dead", false);
            session.setAttribute("health", INITIAL_HEALTH);
            session.setAttribute("area", Area.BADLANDS);
        }

        getServletContext().getRequestDispatcher("/start.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String action = request.getParameter("action");
        int health = (int) session.getAttribute("health");
        boolean foodFound = (boolean) session.getAttribute("foodFound");
        Area area = (Area) session.getAttribute("area");

        health--;

        if (action != null) {
            switch (action) {
                case "explore" -> {
                    health -= 5;
                    if (area.equals(Area.CAVE)) {
                        session.setAttribute("keyFound", true);
                    }
                    if (area.equals(Area.WATERFALL)) {
                        session.setAttribute("portalFound", true);
                    }
                    if (area.equals(Area.FOREST)) {
                        session.setAttribute("toolFound", true);
                    }
                }
                case "searchFood" -> {
                    if (!foodFound && !area.equals(Area.BADLANDS) && !area.equals(Area.PORTAL)) {
                        health += 20;
                        session.setAttribute("foodFound", true);
                    }
                }
                case "badlands" -> session.setAttribute("area", Area.BADLANDS);
                case "forest" -> session.setAttribute("area", Area.FOREST);
                case "cave" -> {
                    if ((boolean) session.getAttribute("toolFound")) {
                        health -= 10;
                    } else {
                        health -= 30;
                    }
                    session.setAttribute("area", Area.CAVE);
                }
                case "deepForest" -> {
                    if ((boolean) session.getAttribute("toolFound")) {
                        health -= 20;
                    } else {
                        health -= 60;
                    }
                    session.setAttribute("area", Area.DEEPFOREST);
                }
                case "waterfall" -> session.setAttribute("area", Area.WATERFALL);
                case "portal" -> {
                    session.setAttribute("area", Area.PORTAL);
                }
                case "win" -> session.setAttribute("win", true);
            }
        }

        session.setAttribute("health", validateHealth(health));
        response.sendRedirect("start");
    }

    private int validateHealth(int health) {
        if (health < 0) {
            return 0;
        } else if (health > INITIAL_HEALTH) {
            return INITIAL_HEALTH;
        } else {
            return health;
        }
    }
}
