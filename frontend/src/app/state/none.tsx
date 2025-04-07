import {Skeleton} from "@/components/ui/skeleton.tsx";

export function NoState() {
    return (
        <div className="mt-3 space-y-3">
            <Skeleton className="h-8 rounded-md" />
            <Skeleton className="w-20 h-5 rounded-full" />
            <Skeleton className="w-70 h-8 rounded-md" />
            <Skeleton className="w-60 h-5 rounded-full" />
            <div className="flex justify-end">
                <Skeleton className="w-20 h-8 rounded-md" />
            </div>
        </div>
    );
}