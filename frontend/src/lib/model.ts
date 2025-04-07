export interface FlowModel {
    id: string
    extra?: any;
}

export interface AccountModel {
    id: string
    name: string
    imageUrl: string
}

interface IdleStateModel {
    type: "IDLE";
    account: null;
}

interface OnlineStateModel {
    type: "LOGGED_IN" | "CONNECTED";
    account: AccountModel;
}

export type StateModel = IdleStateModel | OnlineStateModel;

export interface GuildModel {
    id: string
    name: string
    iconUrl?: string
    memberCount: number
    voiceChannels: VoiceChannelModel[]
}

export interface VoiceChannelModel {
    id: string
    name: string
    userLimit: number
    bitrate: number
    position: number
    canJoin: boolean
    members: MemberModel[]
}

export interface MemberModel {
    id: string
    name: string
    avatarUrl: string
}

export interface AudioInputModel {
    name: string
    device: string
}

export interface CurrentVoiceChannelModel {
    id: string
    guildId: string
    guildName: string
    guildIconUrl?: string
    name: string
    members: MemberModel[]
    currentInput?: AudioInputModel
}
