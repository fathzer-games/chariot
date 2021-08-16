package chariot.internal.impl;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import chariot.Client.Scope;
import chariot.model.Enums.*;
import chariot.internal.Endpoint;
import chariot.internal.Util;
import chariot.internal.InternalClient;
import chariot.model.Ack;
import chariot.model.ChallengeResult;
import chariot.model.ChallengeResult.ChallengeAI;
import chariot.model.ChallengeResult.Challenge;
import chariot.model.Result;
import chariot.model.StreamEvent;

public class ChallengesAuthCommonImpl extends ChallengesImpl implements Internal.ChallengeAuthCommon {

    private final Scope scope;

    public ChallengesAuthCommonImpl(InternalClient client, Scope scope) {
        super(client);
        this.scope = scope;
    }

    @Override
    public Result<StreamEvent> streamEvents() {
        return streamEvents(scope);
    }

    @Override
    public Result<Challenge> challenge(String userId, InternalChallengeParameters parameters) {
        return challenge(scope, userId, parameters);
    }

    @Override
    public Result<ChallengeAI> challengeAI(InternalChallengeAIParameters parameters) {
        return challengeAI(scope, parameters);
    }

    @Override
    public Result<Ack> cancelChallenge(String challengeId, Optional<Supplier<char[]>> opponentToken) {
        return cancelChallenge(scope, challengeId, opponentToken);
    }

    @Override
    public Result<Ack> acceptChallenge(String challengeId) {
        return acceptChallenge(scope, challengeId);
    }

    @Override
    public Result<Ack> declineChallenge(String challengeId, Optional<DeclineReason> reason) {
        return declineChallenge(scope, challengeId, reason);
    }

    private Result<StreamEvent> streamEvents(Scope scope) {
        var request = Endpoint.streamEvents.newRequest()
            .scope(scope)
            .stream()
            .build();

        return fetchMany(request);
    }

    private Result<Challenge> challenge(Scope scope, String userId, InternalChallengeParameters parameters) {
        var parametersString = Util.urlEncode(parameters.toMap());

        var request = Endpoint.challengeCreate.newRequest()
            .scope(scope)
            .path(userId)
            .post(parametersString)
            .build();

        var result = fetchOne(request);

        if (result instanceof Result.One<ChallengeResult> o && o.entry() instanceof ChallengeResult.ChallengeInfo ci) {
            return Result.one(ci.challenge());
        } else {
            return Result.fail(result.error());
        }
    }

    private Result<ChallengeAI> challengeAI(Scope scope, InternalChallengeAIParameters parameters) {
        var parametersString = Util.urlEncode(parameters.toMap());

        var request = Endpoint.challengeAI.newRequest()
            .scope(scope)
            .post(parametersString)
            .build();

        var result = fetchOne(request);

        if (result instanceof Result.One<ChallengeResult> o && o.entry() instanceof ChallengeAI ai) {
            return Result.one(ai);
        } else {
            return Result.fail(result.error());
        }
    }

    private Result<Ack> cancelChallenge(Scope scope, String challengeId, Optional<Supplier<char[]>> opponentToken) {

        var builder = Endpoint.challengeCancel.newRequest()
            .scope(scope)
            .path(challengeId)
            .post();

        opponentToken.ifPresent(tokenSupplier -> builder.query(Map.of("opponentToken", String.valueOf(tokenSupplier.get()))));

        var request = builder.build();

        return fetchOne(request);
    }

    private Result<Ack> acceptChallenge(Scope scope, String challengeId) {
        var request = Endpoint.challengeAccept.newRequest()
            .scope(scope)
            .post()
            .path(challengeId)
            .build();

        return fetchOne(request);
    }

    private Result<Ack> declineChallenge(Scope scope, String challengeId, Optional<DeclineReason> reason) {
        var builder = Endpoint.challengeDecline.newRequest();
        reason.ifPresent(r -> builder.post("reason=" + r.name()));
        builder.path(challengeId);

        var request = builder.build();

        return fetchOne(request);
    }

}
