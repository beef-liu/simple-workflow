CREATE TABLE `WfInstance` (
  `workflow_id` char(32) NOT NULL,
  `workflow_name` char(64) DEFAULT NULL,
  `workflow_version` bigint(20) DEFAULT NULL,
  `workflow_status` int(11) DEFAULT NULL,
  `current_state_name` char(64) DEFAULT NULL,
  `current_state_id` char(32) DEFAULT NULL,
  `parent_flow_id` char(32) DEFAULT NULL,
  `parent_flow_state_id` char(32) DEFAULT NULL,
  `parent_flow_state_event` char(64) DEFAULT NULL,
  `update_time` bigint(20) DEFAULT NULL,
  `create_time` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`workflow_id`),
  KEY `IDX1` (`workflow_status`,`update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `WfMeta` (
  `name` char(64) NOT NULL,
  `version` bigint(20) NOT NULL,
  `description` varchar(4000) DEFAULT NULL,
  `author` char(32) DEFAULT NULL,
  `engineName` char(128) DEFAULT NULL,
  `startState` char(64) DEFAULT NULL,
  `states` longtext,
  PRIMARY KEY (`name`,`version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `WfStateInstance` (
  `state_id` char(32) NOT NULL,
  `workflow_id` char(32) NOT NULL,
  `workflow_name` char(64) DEFAULT NULL,
  `state_name` char(64) DEFAULT NULL,
  `in_data` longtext,
  `state_data` longtext,
  `triggered_event` char(64) DEFAULT NULL,
  `triggered_event_data` longtext,
  `triggered_subflow_id` char(32) DEFAULT NULL,
  `to_state_name` char(64) DEFAULT NULL,
  `to_state_id` char(32) DEFAULT NULL,
  `create_user` char(32) DEFAULT NULL,
  `update_user` char(32) DEFAULT NULL,
  `create_time` bigint(20) DEFAULT NULL,
  `update_time` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`state_id`),
  KEY `IDX1` (`workflow_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `WfTraceRecord` (
  `workflow_id` char(32) NOT NULL,
  `trace_seq` bigint(20) NOT NULL,
  `parent_workflow_id` char(32) DEFAULT NULL,
  `state_id` char(32) DEFAULT NULL,
  `create_time` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`workflow_id`,`trace_seq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
