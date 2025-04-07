import {FlowModel} from "@/lib/model.ts";
import { toast } from "sonner";

type FlowListener = (flow: FlowModel) => void;

const predicate = {
    exact: (id: string, block: FlowListener) => (flow: FlowModel) => {
        if (flow.id === id) block(flow);
    },
    regex: (regex: RegExp, block: FlowListener) => (flow: FlowModel) => {
        if (regex.test(flow.id)) block(flow);
    },
    wildcard: (hint: string, block: FlowListener) => {
        // Convert string to regex
        hint = hint.replace(/([.*+?^=!:${}()|\[\]\/\\])/g, "\\$1");
        // Add double wildcard support
        hint = hint.replace(/\\\*\*/g, "(?:[^.].)*[^.]+");
        // Add wildcard support
        hint = hint.replace(/\\\*/g, "[^.]+");
        return predicate.regex(new RegExp(hint), block);
    }
}

const listeners: FlowListener[] = [
    predicate.exact("action.login.start", () => {
        toast.loading("Logging in...", { id: "action.login" });
    }),
    predicate.exact("action.login.connecting", () => {
        toast.loading("Connecting...", { id: "action.login" });
    }),
    predicate.exact("action.login.connected", () => {
        toast.loading("Connected to Discord, loading data...", { id: "action.login" });
    }),
    predicate.exact("action.login.success", () => {
        toast.dismiss("action.login");
        toast.success("Logged in successfully");
    }),
    predicate.exact("action.connect.start", () => {
        toast.loading("Connecting to channel...", { id: "action.connect" });
    }),
    predicate.exact("action.connect.joined", () => {
        toast.loading("Joined channel, loading data...", { id: "action.connect" });
    }),
    predicate.exact("action.connect.success", () => {
        toast.dismiss("action.connect");
        toast.success("Connected to channel");
    }),
    predicate.exact("action.stream.start", () => {
        toast.loading("Changing inputs...", { id: "action.stream" });
    }),
    predicate.exact("action.stream.open", () => {
        toast.loading("Connecting to input...", { id: "action.stream" });
    }),
    predicate.exact("action.stream.opened", () => {
        toast.dismiss("action.stream");
        toast.success("Streaming started");
    }),
    predicate.exact("action.stream.close", () => {
        toast.dismiss("action.stream");
        toast.success("Streaming stopped");
    }),
    predicate.exact("action.disconnect.start", () => {
        toast.loading("Disconnecting...", { id: "action.disconnect" });
    }),
    predicate.exact("action.disconnect.close", () => {
        toast.loading("Leaving channel...", { id: "action.disconnect" });
    }),
    predicate.exact("action.disconnect.success", () => {
        toast.dismiss("action.disconnect");
        toast.success("Disconnected from channel");
    }),
    predicate.exact("action.logout.start", () => {
        toast.loading("Logging out...", { id: "action.logout" });
    }),
    predicate.exact("action.logout.disconnect", () => {
        toast.loading("Disconnecting from Discord...", { id: "action.logout" });
    }),
    predicate.exact("action.logout.success", () => {
        toast.dismiss("action.logout");
        toast.success("Logged out successfully");
    }),
];

export function onFlow(flow: FlowModel) {
    // Notify all listeners
    listeners.forEach(listener => listener(flow));
}