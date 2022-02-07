package chariot.api;

import chariot.model.Ack;
import chariot.model.Result;
import chariot.model.TeamRequest;

public interface TeamsAuth extends Teams {

    Result<Ack> joinTeam(String teamId);
    Result<Ack> joinTeam(String teamId, String message);
    Result<Ack> joinTeamPW(String teamId, String password);
    Result<Ack> joinTeamPW(String teamId, String password, String message);

    Result<Ack> leaveTeam(String teamId);

    Result<Ack> kickFromTeam(String teamId, String userId);

    Result<Ack> messageTeam(String teamId, String message);

    /**
     * Get pending join requests of your team
     */
    Result<TeamRequest> requests(String teamId);

}
