package org.milyn.smooks.edi.repository;

import org.milyn.assertion.AssertArgument;
import org.milyn.container.ExecutionContext;

import java.util.Map;
import java.util.HashMap;

/**
 * BindingGroupRepository stores and fetches {@link org.milyn.smooks.edi.repository.BindingGroup} from the
 * {@link org.milyn.container.ExecutionContext}.
 * @see BindingGroup
 * @author bardl
 */
public class BindingGroupRepository {

    private static final String SEGMENT_REPOSITORY_CONTEXT_KEY = BindingGroupRepository.class.getName() + "#CONTEXT_KEY";

    private final Map<String, BindingGroup> segmentMap;

	public BindingGroupRepository() {
		this.segmentMap = new HashMap<String, BindingGroup>();
	}

    public static BindingGroupRepository getSegmentRepository(ExecutionContext executionContext) {
		BindingGroupRepository segmentRepository = (BindingGroupRepository) executionContext.getAttribute(SEGMENT_REPOSITORY_CONTEXT_KEY);

		if(segmentRepository == null) {
			segmentRepository = new BindingGroupRepository();
			executionContext.setAttribute(SEGMENT_REPOSITORY_CONTEXT_KEY, segmentRepository);
		}

		return segmentRepository;
	}

	public BindingGroup getSegment(String segmentId) {
		AssertArgument.isNotNull(segmentId, "segmentId");

        BindingGroup segmentBatch = segmentMap.get(segmentId);

        if (segmentBatch == null) {
            segmentBatch = new BindingGroup();
            segmentMap.put(segmentId, segmentBatch);
        }

        return segmentBatch;
	}

    public void removeSegment(String segmentId) {
    	segmentMap.remove(segmentId);
    }

}
