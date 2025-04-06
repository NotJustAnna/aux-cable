import {Card, DangerButton, SuccessButton} from "./common.tsx";
import {GuildModel, StateModel} from "../model.ts";
import {useWebSocket} from "../contexts/WebSocketContext.tsx";
import {useState} from "react";
import {useMountEffect} from "../useMountEffect.ts";
import {useHttp} from "../contexts/HttpContext.ts";
import {errorStrings} from "../errorStrings.ts";
import {usePostMessage} from "../contexts/MessageContext.tsx";
import {faRightToBracket} from "@fortawesome/free-solid-svg-icons/faRightToBracket";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

interface Props {
    state: StateModel
}

export function LoggedInPage({state}: Props) {
    const postMessage = usePostMessage();
    const ws = useWebSocket();
    const http = useHttp();

    const [guilds, setGuilds] = useState<GuildModel[] | null>(null);
    useMountEffect(() => ws.subscribe("guilds", (data: GuildModel[]) => setGuilds(data)));

    const logout = () => {
        http.post('/actions/logout', {}).then(res => {
            if (res.status >= 400 && res.status <= 500) {
                postMessage({ type: 'ERROR', content: errorStrings[res.data.errorType] ?? res.data.errorType });
            }
        })
    };

    const connect = (channelId: string) => {
        http.post('/actions/connect', { channelId }).then(res => {
            if (res.status >= 400 && res.status <= 500) {
                postMessage({ type: 'ERROR', content: errorStrings[res.data.errorType] ?? res.data.errorType });
            }
        })
    };

    const copyInviteLink = () => {
        http.get('/invite').then(res => {
            if (res.status >= 400 && res.status <= 500) {
                postMessage({ type: 'ERROR', content: errorStrings[res.data.errorType] ?? res.data.errorType });
            } else {
                navigator.clipboard.writeText(res.data);
                postMessage({ type: 'INFO', content: "Invite link copied to clipboard!" });
            }
        })

    }

    const account = state.account!;
    return <Card>
        <div className="px-3 flex gap-3 justify-items-center">
            <img src={account.imageUrl} alt="" className="rounded-full max-w-10 max-h-10 aspect-square"/>
            <div className="flex flex-1 flex-col justify-center">
                <h2 className="mb-0.5 text-xl font-semibold">{account.name}</h2>
            </div>
            <DangerButton size="sm" onClick={logout}>Logout</DangerButton>
        </div>
        {
            guilds !== null && <>
                {
                    guilds.map(guild => <div className="flex gap-3 text-white border border-slate-700 rounded-md p-4 my-4">
                        <img src={guild.iconUrl} alt="" className="rounded-full max-w-10 max-h-10 aspect-square"/>
                        <div className="flex-1">
                            <h3 className="mt-0.75 text-lg font-semibold">{guild.name}</h3>
                            {guild.voiceChannels.map(channel => <div className="flex gap-2.5 justify-items-center ps-2 pt-1">
                                <span className="text-slate-400 font-bold text-xl font-mono">#</span>
                                <div className="flex flex-col justify-center flex-1">
                                    <h2>{channel.name}</h2>
                                </div>
                                <SuccessButton size="sm" onClick={() => connect(channel.id)}><FontAwesomeIcon icon={faRightToBracket} /></SuccessButton>
                            </div>)}
                        </div>
                    </div>)
                }
                {
                    guilds.length > 0 && <div className="flex justify-center items-center gap-4">
                        <div className="text-lg font-semibold">Server not here?</div>
                        <SuccessButton onClick={copyInviteLink}>Invite</SuccessButton>
                    </div> || <div className="p-4 pb-0 text-center">
                            <h2 className="text-lg text-center font-semibold">No servers found.</h2>
                            <p className="text-center text-slate-400 text-balance mb-3">Once you add the bot to a server, it will show up here!</p>
                            <SuccessButton onClick={copyInviteLink}>Invite</SuccessButton>
                        </div>
                }
            </> || <div className="p-4 text-lg font-semibold text-center">
                Retrieving guilds...
            </div>
        }
    </Card>
}