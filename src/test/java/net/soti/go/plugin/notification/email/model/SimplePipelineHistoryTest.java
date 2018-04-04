package net.soti.go.plugin.notification.email.model;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: wsim
 * Date: 2018-04-03
 */
public class SimplePipelineHistoryTest {
    private static final String testData = " " +
            "{ " +
            "\"pagination\": " +
            "{ " +
            "\"offset\": 0, " +
            "\"total\": 122, " +
            "\"page_size\": 1 " +
            "}, " +
            "\"pipelines\": [ " +
            "{ " +
            "\"label\": \"122\", " +
            "\"name\": \"UAT_v1412\", " +
            "\"natural_order\": 122.0, " +
            "\"can_run\": true, " +
            "\"stages\": [ " +
            "{ " +
            "\"result\": \"Passed\", " +
            "\"jobs\": [ " +
            "{ " +
            "\"state\": \"Completed\", " +
            "\"result\": \"Passed\", " +
            "\"name\": \"UploadInstallerAndToolsToS3\", " +
            "\"id\": 181656, " +
            "\"scheduled_date\": 1522137843621 " +
            "} " +
            "], " +
            "\"name\": \"UploadToS3Stage\", " +
            "\"rerun_of_counter\": null, " +
            "\"approval_type\": \"success\", " +
            "\"scheduled\": true, " +
            "\"operate_permission\": true, " +
            "\"approved_by\": \"changes\", " +
            "\"can_run\": true, " +
            "\"id\": 78844, " +
            "\"counter\": \"1\" " +
            "}, " +
            "{ " +
            "\"jobs\": [], " +
            "\"name\": \"CreateInstance\", " +
            "\"rerun_of_counter\": null, " +
            "\"approval_type\": null, " +
            "\"scheduled\": false, " +
            "\"operate_permission\": true, " +
            "\"approved_by\": null, " +
            "\"can_run\": true, " +
            "\"id\": 0, " +
            "\"counter\": \"1\" " +
            "}, " +
            "{ " +
            "\"jobs\": [], " +
            "\"name\": \"DestroyInstances\", " +
            "\"rerun_of_counter\": null, " +
            "\"approval_type\": null, " +
            "\"scheduled\": false, " +
            "\"operate_permission\": true, " +
            "\"approved_by\": null, " +
            "\"can_run\": false, " +
            "\"id\": 0, " +
            "\"counter\": \"1\" " +
            "} " +
            "], " +
            "\"id\": 64321, " +
            "\"build_cause\": " +
            "{ " +
            "\"trigger_message\": \"triggered by Acceptance_v1412/556/PublishStage/1\", " +
            "\"approver\": \"\", " +
            "\"material_revisions\": [ " +
            "{ " +
            "\"material\": " +
            "{ " +
            "\"fingerprint\": \"194e482604c4d5e5b188c784aeeece4ae28add027fce79a27973f38bd3594002\", " +
            "\"description\": \"Acceptance_v1412\", " +
            "\"id\": 22791, " +
            "\"type\": \"SimplePipeline\" " +
            "}, " +
            "\"modifications\": [ " +
            "{ " +
            "\"modified_time\": 1522137839152, " +
            "\"user_name\": \"Unknown\", " +
            "\"id\": 65250, " +
            "\"revision\": \"Acceptance_v1412/556/PublishStage/1\", " +
            "\"email_address\": null, " +
            "\"comment\": \"Unknown\" " +
            "} " +
            "], " +
            "\"changed\": true " +
            "}, " +
            "{ " +
            "\"material\": " +
            "{ " +
            "\"fingerprint\": \"2bf304a99889cf8a24e8531ece8ef06e9f155fad7732cd227cded35c1fbfe174\", " +
            "\"description\": \"URL: http://serv_gocd:******@taipan.corp.soti.net:8080/tfs/SOTITFS/MobiControl/_git/UATonAWS, Branch: v14.1.2\", " +
            "\"id\": 23381, " +
            "\"type\": \"Git\" " +
            "}, " +
            "\"modifications\": [ " +
            "{ " +
            "\"modified_time\": 1521983700000, " +
            "\"user_name\": \"Wooseung Sim \\u003CWooseung.Sim@soti.net\\u003E\", " +
            "\"id\": 64778, " +
            "\"revision\": \"1a293f06388e6dce8e50685c2a0b5ca397efdbfc\", " +
            "\"email_address\": null, " +
            "\"comment\": \"Polished instance wait time\" " +
            "} " +
            "], " +
            "\"changed\": false " +
            "} " +
            "], " +
            "\"trigger_forced\": false " +
            "}, " +
            "\"preparing_to_schedule\": false, " +
            "\"counter\": 122, " +
            "\"comment\": null " +
            "}, " +
            "{ " +
            "\"label\": \"121\", " +
            "\"name\": \"UAT_v1412\", " +
            "\"natural_order\": 121.0, " +
            "\"can_run\": true, " +
            "\"stages\": [ " +
            "{ " +
            "\"result\": \"Passed\", " +
            "\"jobs\": [ " +
            "{ " +
            "\"state\": \"Completed\", " +
            "\"result\": \"Passed\", " +
            "\"name\": \"UploadInstallerAndToolsToS3\", " +
            "\"id\": 180778, " +
            "\"scheduled_date\": 1522096004514 " +
            "} " +
            "], " +
            "\"name\": \"UploadToS3Stage\", " +
            "\"rerun_of_counter\": null, " +
            "\"approval_type\": \"success\", " +
            "\"scheduled\": true, " +
            "\"operate_permission\": true, " +
            "\"approved_by\": \"changes\", " +
            "\"can_run\": true, " +
            "\"id\": 78424, " +
            "\"counter\": \"1\" " +
            "}, " +
            "{ " +
            "\"jobs\": [], " +
            "\"name\": \"CreateInstance\", " +
            "\"rerun_of_counter\": null, " +
            "\"approval_type\": null, " +
            "\"scheduled\": false, " +
            "\"operate_permission\": true, " +
            "\"approved_by\": null, " +
            "\"can_run\": true, " +
            "\"id\": 0, " +
            "\"counter\": \"1\" " +
            "}, " +
            "{ " +
            "\"jobs\": [], " +
            "\"name\": \"DestroyInstances\", " +
            "\"rerun_of_counter\": null, " +
            "\"approval_type\": null, " +
            "\"scheduled\": false, " +
            "\"operate_permission\": true, " +
            "\"approved_by\": null, " +
            "\"can_run\": false, " +
            "\"id\": 0, " +
            "\"counter\": \"1\" " +
            "} " +
            "], " +
            "\"id\": 63988, " +
            "\"build_cause\": " +
            "{ " +
            "\"trigger_message\": \"triggered by Acceptance_v1412/554/PublishStage/1\", " +
            "\"approver\": \"\", " +
            "\"material_revisions\": [ " +
            "{ " +
            "\"material\": " +
            "{ " +
            "\"fingerprint\": \"194e482604c4d5e5b188c784aeeece4ae28add027fce79a27973f38bd3594002\", " +
            "\"description\": \"Acceptance_v1412\", " +
            "\"id\": 22791, " +
            "\"type\": \"SimplePipeline\" " +
            "}, " +
            "\"modifications\": [ " +
            "{ " +
            "\"modified_time\": 1522095993013, " +
            "\"user_name\": \"Unknown\", " +
            "\"id\": 64950, " +
            "\"revision\": \"Acceptance_v1412/554/PublishStage/1\", " +
            "\"email_address\": null, " +
            "\"comment\": \"Unknown\" " +
            "} " +
            "], " +
            "\"changed\": true " +
            "}, " +
            "{ " +
            "\"material\": " +
            "{ " +
            "\"fingerprint\": \"2bf304a99889cf8a24e8531ece8ef06e9f155fad7732cd227cded35c1fbfe174\", " +
            "\"description\": \"URL: http://serv_gocd:******@taipan.corp.soti.net:8080/tfs/SOTITFS/MobiControl/_git/UATonAWS, Branch: v14.1.2\", " +
            "\"id\": 23381, " +
            "\"type\": \"Git\" " +
            "}, " +
            "\"modifications\": [ " +
            "{ " +
            "\"modified_time\": 1521983700000, " +
            "\"user_name\": \"Wooseung Sim \\u003CWooseung.Sim@soti.net\\u003E\", " +
            "\"id\": 64778, " +
            "\"revision\": \"1a293f06388e6dce8e50685c2a0b5ca397efdbfc\", " +
            "\"email_address\": null, " +
            "\"comment\": \"Polished instance wait time\" " +
            "} " +
            "], " +
            "\"changed\": false " +
            "} " +
            "], " +
            "\"trigger_forced\": false " +
            "}, " +
            "\"preparing_to_schedule\": false, " +
            "\"counter\": 121, " +
            "\"comment\": null " +
            "}, " +
            "{ " +
            "\"label\": \"120\", " +
            "\"name\": \"UAT_v1412\", " +
            "\"natural_order\": 120.0, " +
            "\"can_run\": true, " +
            "\"stages\": [ " +
            "{ " +
            "\"result\": \"Passed\", " +
            "\"jobs\": [ " +
            "{ " +
            "\"state\": \"Completed\", " +
            "\"result\": \"Passed\", " +
            "\"name\": \"UploadInstallerAndToolsToS3\", " +
            "\"id\": 180455, " +
            "\"scheduled_date\": 1522083769498 " +
            "} " +
            "], " +
            "\"name\": \"UploadToS3Stage\", " +
            "\"rerun_of_counter\": null, " +
            "\"approval_type\": \"success\", " +
            "\"scheduled\": true, " +
            "\"operate_permission\": true, " +
            "\"approved_by\": \"changes\", " +
            "\"can_run\": true, " +
            "\"id\": 78224, " +
            "\"counter\": \"1\" " +
            "}, " +
            "{ " +
            "\"jobs\": [], " +
            "\"name\": \"CreateInstance\", " +
            "\"rerun_of_counter\": null, " +
            "\"approval_type\": null, " +
            "\"scheduled\": false, " +
            "\"operate_permission\": true, " +
            "\"approved_by\": null, " +
            "\"can_run\": true, " +
            "\"id\": 0, " +
            "\"counter\": \"1\" " +
            "}, " +
            "{ " +
            "\"jobs\": [], " +
            "\"name\": \"DestroyInstances\", " +
            "\"rerun_of_counter\": null, " +
            "\"approval_type\": null, " +
            "\"scheduled\": false, " +
            "\"operate_permission\": true, " +
            "\"approved_by\": null, " +
            "\"can_run\": false, " +
            "\"id\": 0, " +
            "\"counter\": \"1\" " +
            "} " +
            "], " +
            "\"id\": 63823, " +
            "\"build_cause\": " +
            "{ " +
            "\"trigger_message\": \"modified by Wooseung Sim \\u003CWooseung.Sim@soti.net\\u003E\", " +
            "\"approver\": \"\", " +
            "\"material_revisions\": [ " +
            "{ " +
            "\"material\": " +
            "{ " +
            "\"fingerprint\": \"194e482604c4d5e5b188c784aeeece4ae28add027fce79a27973f38bd3594002\", " +
            "\"description\": \"Acceptance_v1412\", " +
            "\"id\": 22791, " +
            "\"type\": \"SimplePipeline\" " +
            "}, " +
            "\"modifications\": [ " +
            "{ " +
            "\"modified_time\": 1522068112728, " +
            "\"user_name\": \"Unknown\", " +
            "\"id\": 64605, " +
            "\"revision\": \"Acceptance_v1412/550/PublishStage/1\", " +
            "\"email_address\": null, " +
            "\"comment\": \"Unknown\" " +
            "} " +
            "], " +
            "\"changed\": false " +
            "}, " +
            "{ " +
            "\"material\": " +
            "{ " +
            "\"fingerprint\": \"2bf304a99889cf8a24e8531ece8ef06e9f155fad7732cd227cded35c1fbfe174\", " +
            "\"description\": \"URL: http://serv_gocd:******@taipan.corp.soti.net:8080/tfs/SOTITFS/MobiControl/_git/UATonAWS, Branch: v14.1.2\", " +
            "\"id\": 23381, " +
            "\"type\": \"Git\" " +
            "}, " +
            "\"modifications\": [ " +
            "{ " +
            "\"modified_time\": 1521983700000, " +
            "\"user_name\": \"Wooseung Sim \\u003CWooseung.Sim@soti.net\\u003E\", " +
            "\"id\": 64778, " +
            "\"revision\": \"1a293f06388e6dce8e50685c2a0b5ca397efdbfc\", " +
            "\"email_address\": null, " +
            "\"comment\": \"Polished instance wait time\" " +
            "} " +
            "], " +
            "\"changed\": true " +
            "} " +
            "], " +
            "\"trigger_forced\": false " +
            "}, " +
            "\"preparing_to_schedule\": false, " +
            "\"counter\": 120, " +
            "\"comment\": null " +
            "} " +
            "] " +
            "} ";

    @Test
    public void shouldDeserializePipelineHistory() {
        PipelineHistory history = PipelineHistory.fromJson(testData);

        assertThat(history.pipelines.length, is(3));
    }
}
