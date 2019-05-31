package com.pinterest.doctorkafka.servlet;


import com.pinterest.doctorkafka.DoctorKafkaMain;
import com.pinterest.doctorkafka.KafkaClusterManager;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class DoctorKafkaInfoServlet extends DoctorKafkaServlet {

  private static final Logger LOG = LogManager.getLogger(DoctorKafkaInfoServlet.class);
  private static final Gson gson = new Gson();

  @Override
  public void renderHTML(PrintWriter writer, Map<String, String> params) {
    try {
      double jvmUpTimeInSeconds = ManagementFactory.getRuntimeMXBean().getUptime() / 1000.0;
      String version = getVersion();
      writer.print("<div>");
      writer.print("<p> Version: " + version + ", Uptime: " + jvmUpTimeInSeconds + " seconds </p>");
      writer.print("</div>");

      Collection<KafkaClusterManager> clusterManagers = DoctorKafkaMain.doctorKafka.getClusterManagers();
      writer.print("<div> ");
      writer.print("<table class=\"table table-responsive\"> ");
      writer.print("<th> ClusterName </th> <th> Size </th> <th> Under-replicated Partitions</th>");
      writer.print("<th> Maintenance Mode </th>");
      writer.print("<tbody>");

      Map<String, String> clustersHtml = new TreeMap<>();
      for (KafkaClusterManager clusterManager : clusterManagers) {
        String clusterName = clusterManager.getClusterName();
        String htmlStr;
        htmlStr = "<tr> <td> <a href=\"/servlet/clusterinfo?name=" + clusterName + "\">"
              + clusterName + "</a>"
              + " </td> <td> " + ((clusterManager.getCluster()!=null)? clusterManager.getClusterSize() : "no brokerstats")
              + " </td> <td> <a href=\"/servlet/urp?cluster=" + clusterName + "\">"
              + clusterManager.getUnderReplicatedPartitions().size() + "</a> </td>"
              + "<td>" + clusterManager.isMaintenanceModeEnabled() + " </td> </tr>";

        clustersHtml.put(clusterName, htmlStr);
      }
      for (Map.Entry<String, String> entry : clustersHtml.entrySet()) {
        writer.print(entry.getValue());
      }
      writer.print("</tbody> </table>");
      writer.print("</div>");
    } catch (Exception e) {
      LOG.error("Exception in getting info", e);
      writer.println(e);
    }
  }
}
