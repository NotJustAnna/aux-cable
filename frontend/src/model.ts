
export interface MessageModel {
    type: "INFO" | "WARNING" | "SUCCESS" | "ERROR"
    content: string
}

export interface AccountModel {
    id: string
    name: string
    imageUrl: string
}

export interface StateModel {
    type: "IDLE" |"LOGGED_IN" |"CONNECTED"
    account?: AccountModel
}

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