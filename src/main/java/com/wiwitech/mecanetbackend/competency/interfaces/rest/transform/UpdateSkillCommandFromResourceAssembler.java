package com.wiwitech.mecanetbackend.competency.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.competency.domain.model.commands.UpdateSkillCommand;
import com.wiwitech.mecanetbackend.competency.interfaces.rest.resources.UpdateSkillResource;

/** Converts REST payload → UpdateSkillCommand. */
public class UpdateSkillCommandFromResourceAssembler {
    public static UpdateSkillCommand toCommand(Long id, UpdateSkillResource r) {
        return new UpdateSkillCommand(id, r.name(), r.description(), r.category());
    }
}
