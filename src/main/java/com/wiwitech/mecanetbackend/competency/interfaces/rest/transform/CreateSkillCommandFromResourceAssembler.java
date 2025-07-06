package com.wiwitech.mecanetbackend.competency.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.competency.domain.model.commands.CreateSkillCommand;
import com.wiwitech.mecanetbackend.competency.interfaces.rest.resources.CreateSkillResource;

/** Converts REST payload → CreateSkillCommand. */
public class CreateSkillCommandFromResourceAssembler {
    public static CreateSkillCommand toCommand(CreateSkillResource r) {
        return new CreateSkillCommand(r.name(), r.description(), r.category());
    }
}